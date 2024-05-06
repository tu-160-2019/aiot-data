package com.ai.tts.util;

import cn.hutool.core.util.ArrayUtil;

/**
 * #### symbol
 * 音素标记。
 * 中文音素，简单英文音素，简单中文音素。
 */
public class SymbolUtils {
    static String _pad = "_"; //  # 填充符 - Padding symbol
    static String _eos = "~"; //  # 结束符 - End-of-sequence symbol
    static String _chain = "-";// # 连接符，连接读音单位 - Connects pronunciation units
    static String _oov = "*";

    // 中文音素表 - Chinese phonetic symbols
    // 声母：27 - 27 initial sounds
    static String[] _shengmu = {
            "aa", "b", "c", "ch", "d", "ee", "f", "g", "h", "ii", "j", "k", "l", "m", "n", "oo", "p", "q", "r", "s", "sh",
            "t", "uu", "vv", "x", "z", "zh"
    };
    // 韵母：41 - 41 final sounds
    static String[] _yunmu = {
            "a", "ai", "an", "ang", "ao", "e", "ei", "en", "eng", "er", "i", "ia", "ian", "iang", "iao", "ie", "in", "ing",
            "iong", "iu", "ix", "iy", "iz", "o", "ong", "ou", "u", "ua", "uai", "uan", "uang", "ueng", "ui", "un", "uo", "v",
            "van", "ve", "vn", "ng", "uong"
    };
    // 声调：5 - 5 tones
    static String[] _shengdiao = {"1", "2", "3", "4", "5"};
    // 字母：26 - English phonetic symbols: 26 letters
    static String[] _alphabet = "Aa Bb Cc Dd Ee Ff Gg Hh Ii Jj Kk Ll Mm Nn Oo Pp Qq Rr Ss Tt Uu Vv Ww Xx Yy Zz".split(" ");
    // 英文：26
    static String[] _english = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    // 标点：10 - Punctuation: 10 symbols
    static String[] _biaodian = "! ? . , ; : \" # ( )".split(" ");
    // 注：!=!！|?=?？|.=.。|,=,，、|;=;；|:=:：|"="“|#= \t|(=(（[［{｛【<《|)=)）]］}｝】>》
    // 其他：7 - Other symbols: 7 symbols
    static String[] _other = "w y 0 6 7 8 9".split(" ");
    // 大写字母：26 - Uppercase letters
    static String[] _upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    // 小写字母：26 -  Lowercase letters
    static String[] _lower = "abcdefghijklmnopqrstuvwxyz".split("");
    // 标点符号：12 - Punctuation symbols
    static String[] _punctuation = "! ' \" ( ) , - . : ; ?".split(" ");

    // 数字：10 - Digits
    static String[] _digit = "0123456789".split("");
    // 字母和符号：64 - English letters and symbols
    // 用于英文:ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s
    static String[] _character_en = ArrayUtil.addAll(_upper, _lower, _punctuation);

    // 字母、数字和符号：74 - Chinese and English letters and symbols: 74 symbols
    // 用于英文或中文:ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s0123456789
    static String[] _character_cn = ArrayUtil.addAll(_upper, _lower, _punctuation, _digit);

    // 中文音素：145 - Chinese phonetic symbols: 145 symbols
    // 支持中文环境、英文环境、中英混合环境，中文把文字转为清华大学标准的音素表示
    static String[] symbol_chinese = ArrayUtil.addAll(new String[]{_pad, _eos, _chain}, _shengmu, _yunmu, _shengdiao, _alphabet, _english, _biaodian, _other);

    // 简单英文音素：66 - Simple English phonetic symbols: 66 symbols
    // 支持英文环境 - Supports English environments.
    // ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s
    static String[] symbol_english_simple = ArrayUtil.addAll(new String[]{_pad, _eos}, _upper, _lower, _punctuation);

    // 简单中文音素：76 - Simple Chinese phonetic symbols: 76 symbols
    // 支持英文、中文环境，中文把文字转为拼音字符串 - Supports Chinese and English environments. Converts Chinese characters into pinyin strings.
    // ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s0123456789
    static String[] symbol_chinese_simple = ArrayUtil.addAll(new String[]{_pad, _eos}, _upper, _lower, _punctuation, _digit);


	/*static{
		//英文
		System.arraycopy(_upper, 0, _character_en, 0, _upper.length);
		System.arraycopy(_lower, 0, _character_en, _upper.length, _lower.length);
	}*/

}
