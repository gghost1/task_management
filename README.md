# Task manadgment
## Описание
Приложение направлено для управления задачами
## Запуск
На вашем компьютере должен быть установлен JDK, Apache Maven и Docker
- Склонируйте репозиторий на свой компьютер
  ```bash
   git clone https://github.com/gghost1/task_menedger.git
- Запустите Docker
- Выполните команды:
  ```bash
  mvn clean package
  docker-compose up --build
Эти команды запустят тесты приложения(для тестов поднимается отдельный контейнер), а после 2 контейнера(базу данных и приложение)
## Документация Swagger UI
Документация доступна по [адресу](http://localhost:8080/swagger-ui/index.html) после запуска приложения
