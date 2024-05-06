package com.ai.tts.service.impl;

import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.MapstructUtils;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.tts.service.TtsService;
import com.ai.tts.speaker.SpeakerEncoder;
import com.ai.tts.tacotron2.Tacotron2Encoder;
import com.ai.tts.util.WaveGlowEncoder;
import com.ai.tts.util.DenoiserEncoder;
import com.ai.tts.util.SequenceUtils;
import com.ai.tts.util.FfmpegUtils;
import com.ai.tts.util.AudioUtils;
import com.ai.tts.util.SoundUtils;
import com.ai.tts.dao.DataTtsMapper;
import com.ai.tts.entity.DataTtsEntity;
import com.ai.common.config.AiConfig;
import com.ai.tts.query.DataTtsQuery;
import com.ai.tts.vo.DataTtsVo;
import com.ai.tts.convert.DataTtsConvert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class TtsServiceImpl extends BaseServiceImpl<DataTtsMapper, DataTtsEntity> implements TtsService {

    @Autowired
    private DataTtsMapper dataTtsMapper;
    @Autowired
    private AiConfig aiConfig;

    private static int partials_n_frames = 160;

    @Override
    public PageResult<DataTtsVo> page(DataTtsQuery query) {
        IPage<DataTtsEntity> page = dataTtsMapper.selectPage(getPage(query), getWrapper(query));

//        return new PageResult<>(MapstructUtils.convert(page.getRecords(), DataTtsVo.class), page.getTotal());
        return new PageResult<>(DataTtsConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
    }

    public void delete(List<Long> idList) {
        removeByIds(idList);
    }

    public void sv2tts(String text) {
        Map output = composition(text);
        String path = (String) output.get("path");

        DataTtsEntity entity = new DataTtsEntity();
        entity.setText(text);
        entity.setTtsPath(path);
        entity.setSize((Long) output.get("size"));
        entity.setType(2);

        String url = path.replace(aiConfig.getPath(), aiConfig.getDomain() + "/upload");
        entity.setFileUrl(url);
        dataTtsMapper.insert(entity);
    }

    private Map composition(String text) {
        log.info("Text: {}", text);
        // 目标音色保存路径
        Path audioFile = Paths.get(aiConfig.getTtsVoice());
        // 语音保存路径
        File outs = new File(Paths.get(aiConfig.getTtsOutputPath() + "/" + System.currentTimeMillis() + ".wav").toString());

        NDManager manager = NDManager.newBaseManager(Device.cpu());

        SpeakerEncoder speakerEncoder = new SpeakerEncoder();
        Tacotron2Encoder tacotron2Encoder = new Tacotron2Encoder();
        WaveGlowEncoder waveGlowEncoder = new WaveGlowEncoder();
        DenoiserEncoder denoiserEncoder = new DenoiserEncoder();

        try {
            ZooModel<NDArray, NDArray> speakerEncoderModel = ModelZoo.loadModel(speakerEncoder.criteria(aiConfig.getModelPath() + "/tts/sv2tts_waveglow"));
            Predictor<NDArray, NDArray> speakerEncoderPredictor = speakerEncoderModel.newPredictor();

            ZooModel<NDList, NDArray> tacotron2Model = ModelZoo.loadModel(tacotron2Encoder.criteria(aiConfig.getModelPath() + "/tts/sv2tts_waveglow"));
            Predictor<NDList, NDArray> tacotron2Predictor = tacotron2Model.newPredictor();

            ZooModel<NDArray, NDArray> waveGlowModel = ModelZoo.loadModel(waveGlowEncoder.criteria(aiConfig.getModelPath() + "/tts/sv2tts_waveglow"));
            Predictor<NDArray, NDArray> waveGlowPredictor = waveGlowModel.newPredictor();

            ZooModel<NDArray, NDArray> denoiserModel = ModelZoo.loadModel(denoiserEncoder.criteria(aiConfig.getModelPath() + "/tts/sv2tts_waveglow"));
            Predictor<NDArray, NDArray> denoiserPredictor = denoiserModel.newPredictor();

            // 文本转为ID列表
            List<Integer> text_data_org = SequenceUtils.text2sequence(text);
            int[] text_dataa = text_data_org.stream().mapToInt(Integer::intValue).toArray();
            NDArray text_data = manager.create(text_dataa);
            text_data.setName("text");

            // 目标音色作为Speaker Encoder的输入: 使用ffmpeg 将目标音色mp3文件转为wav格式
            NDArray audioArray = FfmpegUtils.load_wav_to_torch(audioFile.toString(), 22050);

            // 提取这段语音的说话人特征（音色）作为Speaker Embedding
            Pair<LinkedList<LinkedList<Integer>>, LinkedList<LinkedList<Integer>>> slices =
                    AudioUtils.compute_partial_slices(audioArray.size(), partials_n_frames, 0.75f, 0.5f);
            LinkedList<LinkedList<Integer>> wave_slices = slices.getLeft();
            LinkedList<LinkedList<Integer>> mel_slices = slices.getRight();
            int max_wave_length = wave_slices.getLast().getLast();
            if (max_wave_length >= audioArray.size()) {
                audioArray = AudioUtils.pad(audioArray, (max_wave_length - audioArray.size()), manager);
            }
            float[][] fframes = AudioUtils.wav_to_mel_spectrogram(audioArray);
            NDArray frames = manager.create(fframes).transpose();
            NDList frameslist = new NDList();
            for (LinkedList<Integer> s : mel_slices) {
                NDArray temp = speakerEncoderPredictor.predict(frames.get(s.getFirst() + ":" + s.getLast()));
                frameslist.add(temp);
            }
            NDArray partial_embeds = NDArrays.stack(frameslist);
            NDArray raw_embed = partial_embeds.mean(new int[]{0});
            // Speaker Embedding
            NDArray speaker_data = raw_embed.div(((raw_embed.pow(2)).sum()).sqrt());

            Shape shape = speaker_data.getShape();
//            log.info("Target voice feature vector Shape: {}", Arrays.toString(shape.getShape()));
//            log.info("Target voice feature vector: {}", Arrays.toString(speaker_data.toFloatArray()));

            // 模型数据
            NDList input = new NDList();
            input.add(text_data);
            input.add(speaker_data);

            // 生成mel频谱数据
            NDArray mels_postnet = tacotron2Predictor.predict(input);
            shape = mels_postnet.getShape();
//            log.info("Mel spectrogram data Shape: {}", Arrays.toString(shape.getShape()));
//            log.info("Mel spectrogram data: {}", Arrays.toString(mels_postnet.toFloatArray()));

            // 生成wav数据
            NDArray wavWithNoise = waveGlowPredictor.predict(mels_postnet);
            NDArray wav = denoiserPredictor.predict(wavWithNoise);
            SoundUtils.saveWavFile(wav.get(0), 1.0f, outs);
//            log.info("Generated wav audio file: {}", outs.toString());

            Map result = new HashMap();
            result.put("path", outs.getPath());
            result.put("size", outs.length());

            // 输出保存文件的完整路径
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Wrapper<DataTtsEntity> getWrapper(DataTtsQuery query) {
        LambdaQueryWrapper<DataTtsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getText()), DataTtsEntity::getText, query.getText());
        wrapper.like(null != query.getTtsPath(), DataTtsEntity::getTtsPath, query.getTtsPath());
        wrapper.eq(null != query.getCreateTime(), DataTtsEntity::getCreateTime, query.getCreateTime());

        wrapper.orderByDesc(DataTtsEntity::getCreateTime);
        return wrapper;
    }
}
