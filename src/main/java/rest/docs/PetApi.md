# PetApi

Все URI перечислены относительно базового пути *https://petstore.swagger.io/v2*

| Метод                                                | HTTP-запрос                       | Описание                                    |
|------------------------------------------------------|-----------------------------------|---------------------------------------------|
| [**addPet**](PetApi.md#addPet)                       | **POST** /pet                     | Добавить нового питомца в магазин           |
| [**deletePet**](PetApi.md#deletePet)                 | **DELETE** /pet/{petId}           | Удалить питомца                             |
| [**findPetsByStatus**](PetApi.md#findPetsByStatus)   | **GET** /pet/findByStatus         | Найти питомцев по статусу                   |
| [**findPetsByTags**](PetApi.md#findPetsByTags)       | **GET** /pet/findByTags           | Найти питомцев по тегам                     |
| [**getPetById**](PetApi.md#getPetById)               | **GET** /pet/{petId}              | Найти питомца по ID                         |
| [**updatePet**](PetApi.md#updatePet)                 | **PUT** /pet                      | Обновить существующего питомца              |
| [**updatePetWithForm**](PetApi.md#updatePetWithForm) | **POST** /pet/{petId}             | Обновить питомца в магазине с данными формы |
| [**uploadFile**](PetApi.md#uploadFile)               | **POST** /pet/{petId}/uploadImage | Загрузить изображение                       |

## addPet

> addPet(body)

Добавить нового питомца в магазин

### Параметры

| Имя      | Тип                     | Описание                                         | Примечания |
|----------|-------------------------|--------------------------------------------------|------------|
| **body** | [**Pet**](model/Pet.md) | Объект питомца, который нужно добавить в магазин |            |

### Тип возвращаемого значения

null (пустое тело ответа)

### Авторизация

[petstore_auth](README.md#petstore_auth)

### Заголовки HTTP-запроса

- **Content-Type**: application/json, application/xml
- **Accept**: Не определено

### Детали HTTP-ответа

| Код состояния | Описание      | Заголовки ответа |
|---------------|---------------|------------------|
| **405**       | Неверный ввод | -                |

## deletePet

> deletePet(petId, apiKey)

Удалить питомца

### Параметры

| Имя        | Тип        | Описание                | Примечания       |
|------------|------------|-------------------------|------------------|
| **petId**  | **Long**   | ID питомца для удаления |                  |
| **apiKey** | **String** |                         | [необязательный] |

### Тип возвращаемого значения

null (пустое тело ответа)

### Авторизация

[petstore_auth](README.md#petstore_auth)

### Заголовки HTTP-запроса

- **Content-Type**: Не определено
- **Accept**: Не определено

### Детали HTTP-ответа

| Код состояния | Описание          | Заголовки ответа |
|---------------|-------------------|------------------|
| **400**       | Неверный ID       | -                |
| **404**       | Питомец не найден | -                |

## findPetsByStatus

> List&lt;Pet&gt; findPetsByStatus(status)

Найти питомцев по статусу

Можно предоставить несколько значений статуса через запятую

### Параметры

| Имя        | Тип                                 | Описание                          | Примечания                       |
|------------|-------------------------------------|-----------------------------------|----------------------------------|
| **status** | [**List&lt;String&gt;**](String.md) | Статусные значения для фильтрации | [enum: available, pending, sold] |

### Тип возвращаемого значения

[**List&lt;Pet&gt;**](model/Pet.md)

### Авторизация

[petstore_auth](README.md#petstore_auth)

### Заголовки HTTP-запроса

- **Content-Type**: Не определено
- **Accept**: application/json, application/xml

### Детали HTTP-ответа

| Код состояния | Описание                  | Заголовки ответа |
|---------------|---------------------------|------------------|
| **200**       | Успешная операция         | -                |
| **400**       | Неверное значение статуса | -                |

## findPetsByTags

> List&lt;Pet&gt; findPetsByTags(tags)

Найти питомцев по тегам. Можно предоставить несколько тегов через запятую.
Используйте tag1, tag2, tag3 для тестирования.

### Параметры

| Имя      | Тип                                 | Описание            | Примечания |
|----------|-------------------------------------|---------------------|------------|
| **tags** | [**List&lt;String&gt;**](String.md) | Теги для фильтрации |            |

### Тип возвращаемого значения

[**List&lt;Pet&gt;**](model/Pet.md)

### Авторизация

[petstore_auth](README.md#petstore_auth)

### Заголовки HTTP-запроса

- **Content-Type**: Не определено
- **Accept**: application/json, application/xml

### Детали HTTP-ответа

| Код состояния | Описание               | Заголовки ответа |
|---------------|------------------------|------------------|
| **200**       | Успешная операция      | -                |
| **400**       | Неверное значение тега | -                |

## getPetById

> Pet getPetById(petId)

Найти питомца по ID. Возвращает одного питомца.

### Параметры

| Имя       | Тип      | Описание              | Примечания |
|-----------|----------|-----------------------|------------|
| **petId** | **Long** | ID питомца для поиска |            |

### Тип возвращаемого значения

[**Pet**](model/Pet.md)

### Авторизация

[api_key](README.md#api_key)

### Заголовки HTTP-запроса

- **Content-Type**: Не определено
- **Accept**: application/json, application/xml

### Детали HTTP-ответа

| Код состояния | Описание          | Заголовки ответа |
|---------------|-------------------|------------------|
| **200**       | Успешная операция | -                |
| **400**       | Неверный ID       | -                |
| **404**       | Питомец не найден | -                |

## updatePet

> updatePet(body)

Обновить существующего питомца.

### Параметры

| Имя      | Тип                     | Описание                               | Примечания |
|----------|-------------------------|----------------------------------------|------------|
| **body** | [**Pet**](model/Pet.md) | Объект питомца, который нужно обновить |            |

### Тип возвращаемого значения

null (пустое тело ответа)

### Авторизация

[petstore_auth](README.md#petstore_auth)

### Заголовки HTTP-запроса

- **Content-Type**: application/json, application/xml
- **Accept**: Не определено

### Детали HTTP-ответа

| Код состояния | Описание          | Заголовки ответа |
|---------------|-------------------|------------------|
| **400**       | Неверный ID       | -                |
| **404**       | Питомец не найден | -                |
| **405**       | Ошибка проверки   | -                |

## updatePetWithForm

> updatePetWithForm(petId, name, status)

Обновить питомца в магазине с данными формы.

### Параметры

| Имя        | Тип        | Описание                   | Примечания       |
|------------|------------|----------------------------|------------------|
| **petId**  | **Long**   | ID питомца для обновления  |                  |
| **name**   | **String** | Обновленное имя питомца    | [необязательный] |
| **status** | **String** | Обновленный статус питомца | [необязательный] |

### Тип возвращаемого значения

null (пустое тело ответа)

### Авторизация

[petstore_auth](README.md#petstore_auth)

### Заголовки HTTP-запроса

- **Content-Type**: application/x-www-form-urlencoded
- **Accept**: Не определено

### Детали HTTP-ответа

| Код состояния | Описание      | Заголовки ответа |
|---------------|---------------|------------------|
| **405**       | Неверный ввод | -                |

## uploadFile

> ApiResponse uploadFile(petId, additionalMetadata, file)

Загрузить изображение.

### Параметры

| Имя                    | Тип        | Описание                          | Примечания       |
|------------------------|------------|-----------------------------------|------------------|
| **petId**              | **Long**   | ID питомца для обновления         |                  |
| **additionalMetadata** | **String** | Дополнительные данные для сервера | [необязательный] |
| **file**               | **File**   | файл для загрузки                 | [необязательный] |

### Тип возвращаемого значения

[**ApiResponse**](model/ApiResponse.md)

### Авторизация

[petstore_auth](README.md#petstore_auth)

### Заголовки HTTP-запроса

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### Детали HTTP-ответа

| Код состояния | Описание          | Заголовки ответа |
|---------------|-------------------|------------------|
| **200**       | Успешная операция | -                |
