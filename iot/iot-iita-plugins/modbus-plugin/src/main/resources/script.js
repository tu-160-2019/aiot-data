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

this.decode=function(hex){
    try{
        const bytes=hexToByte(hex);
        return {
            "rssi":bytes[0],
            "powerstate":bytes[1]
        };
    }catch(e){
        return {};
    }
}