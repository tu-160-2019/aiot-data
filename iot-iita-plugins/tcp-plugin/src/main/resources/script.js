function hexToByte(hexString) {
    if (hexString.length % 2 !== 0) {
        throw new Error('Invalid hex string. String must have an even number of characters.');
    }

    let byteArray = [];
    for (let i = 0; i < hexString.length; i += 4) {
        byteArray.push(parseInt(hexString.substr(i, 4), 16));
    }

    return byteArray;
}
function byteToHex(bytes) {
    for (var hex = [], i = 0; i < bytes.length; i++) {
        hex.push((bytes[i] >>> 4).toString(16));
        hex.push((bytes[i] & 0xF).toString(16));
    }
    return hex.join("");
}

this.decode=function(data){
    hex=data.payload;
    const bytes=hexToByte(hex);
    return {
        "rssi":bytes[0],
        "powerstate":bytes[1]
    };
}

this.encode=function(params){
    const hex=byteToHex([params.powerstate]);
    return hex;
}