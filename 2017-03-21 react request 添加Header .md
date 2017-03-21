# node.js 添加header
用express 转发HTTP请求时，request中 header 中不能出现中文<br/>
出现中文会产生500内部错误
# 格式

    var headers = {
      'platform' : 'platform',
      'other' : 'other'
    };
    var option =  {
      url: url,
      method: 'POST',
      headers: headers,
      formData: condition
    };
    request(option , fuction callback());
