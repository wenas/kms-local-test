# kms-local-test


## before test

```
docker run -p 8080:8080 \
--mount type=bind,source="$(pwd)"/init,target=/init \
nsmithuk/local-kms
```

## reference

* [local-kms](https://github.com/nsmithuk/local-kms)
* [データキーの暗号化と復号化](https://docs.aws.amazon.com/ja_jp/kms/latest/developerguide/programming-encryption.html)
