# 1. Скопируйте репозиторий
# 2. Установка базы данных:
### В проекте используется PostgreSQL, но так как разработчик может не использовать PostgreSQL, я поставил в файле application.yml spring.profiles.active=test. Это не требует дополнительной настройки базы данных и вы можете переходить к запуску
### Это активизирует application-test.yml, тем самым в приложений будет исользоваться InMemory H2 база данных. Так что, настройка базы даныых не потребуется.
### Для использования своей базы данных вы можете убрать spring.profiles.active=test в application.yml и использовать свой spring.datasource.url, spring.datasource.username, spring.datasource.password.
# 3. После запуска вы можете использовать swagger по ссылке: http://localhost:8080/swagger-ui/index.html 
