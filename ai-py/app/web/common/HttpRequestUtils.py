def parse_request(request):
    """
    解析请求数据并以json形式返回
    引用示例:
    @app.route('/tts', methods=['POST', 'GET'])
    def tts_web():
        部署WEB的API接口
        data = parse_request(request)
        text = data.get('text', '这是个样例')
        speaker = data.get('speaker', 'biaobei')
        audio = data.get('audio', '24')
        wav = sdk_api.tts_sdk(text=text, speaker=speaker, audio=audio)
        return Response(wav, mimetype='audio/wav')
    """
    if request.method == 'POST':
        data = request.json
    elif request.method == 'GET':
        data = request.args
    else:  # POST
        data = request.get_json()
    return data