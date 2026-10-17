j-key-pass
==========

Simple password manager

Простой менеджер паролей.
Очень далекий аналог KeePassX.

## Возможности
* Шифрование AES-256-CBC со случайными солью и IV, ключ выводится через PBKDF2-HMAC-SHA256
* Проверка целостности базы через HMAC-SHA256
* Синхронизация с Dropbox (API v2)
* Генерация паролей (SecureRandom)
* Импорт из KeePassX (XML)

## Требования
* Java 17+
* Maven для сборки

## Сборка
```bash
mvn package
java -jar target/j-key-pass-1.0-SNAPSHOT.jar
```

## Архитектура
```
jkeypass
├── domain       — сущности (Account)
├── crypto       — деривация ключей, шифрование, генератор паролей
├── repository   — формат файла базы, чтение/запись
├── service      — состояние открытой базы, импорт
├── settings     — настройки приложения (Preferences)
├── sync         — интерфейс Synchronizer, реализация Dropbox
├── importer     — парсеры внешних форматов
└── ui           — Swing-интерфейс (окна, диалоги, экшены, AppController)
```

![Главное окно](https://cloud.githubusercontent.com/assets/1154993/14797729/c2f83d64-0b3c-11e6-92fa-19262f000b40.png)
![Окно редактирования записи](https://cloud.githubusercontent.com/assets/1154993/14797746/d2bc490c-0b3c-11e6-94f1-0fbe5886e1df.png)
![Настройки](https://cloud.githubusercontent.com/assets/1154993/14797754/da43dc80-0b3c-11e6-9d79-de4dc6dc0039.png)
![Настройки синхронизации](https://cloud.githubusercontent.com/assets/1154993/14797757/dc2b41e6-0b3c-11e6-8bea-d0f54b354218.png)
