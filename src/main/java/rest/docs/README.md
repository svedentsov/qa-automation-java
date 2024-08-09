## Документация конечных точек API

Все URI относятся к *https://petstore.swagger.io/v2*

| Класс      | Метод                                                                      | HTTP-запрос                       | Описание                                                               |
|------------|----------------------------------------------------------------------------|-----------------------------------|------------------------------------------------------------------------|
| *PetApi*   | [**addPet**](PetApi.md#addPet)                                        | **POST** /pet                     | Добавить нового питомца в магазин                                      |
| *PetApi*   | [**deletePet**](PetApi.md#deletePet)                                  | **DELETE** /pet/{petId}           | Удалить питомца                                                        |
| *PetApi*   | [**findPetsByStatus**](PetApi.md#findPetsByStatus)                    | **GET** /pet/findByStatus         | Найти питомцев по статусу                                              |
| *PetApi*   | [**findPetsByTags**](PetApi.md#findPetsByTags)                        | **GET** /pet/findByTags           | Найти питомцев по тегам                                                |
| *PetApi*   | [**getPetById**](PetApi.md#getPetById)                                | **GET** /pet/{petId}              | Найти питомца по ID                                                    |
| *PetApi*   | [**updatePet**](PetApi.md#updatePet)                                  | **PUT** /pet                      | Обновить информацию о существующем питомце                             |
| *PetApi*   | [**updatePetWithForm**](PetApi.md#updatePetWithForm)                  | **POST** /pet/{petId}             | Обновить информацию о питомце в магазине с использованием данных формы |
| *PetApi*   | [**uploadFile**](PetApi.md#uploadFile)                                | **POST** /pet/{petId}/uploadImage | Загрузить изображение                                                  |
| *StoreApi* | [**deleteOrder**](StoreApi.md#deleteOrder)                            | **DELETE** /store/order/{orderId} | Удалить заказ по ID                                                    |
| *StoreApi* | [**getInventory**](StoreApi.md#getInventory)                          | **GET** /store/inventory          | Возвращает инвентарь питомцев по статусу                               |
| *StoreApi* | [**getOrderById**](StoreApi.md#getOrderById)                          | **GET** /store/order/{orderId}    | Найти заказ по ID                                                      |
| *StoreApi* | [**placeOrder**](StoreApi.md#placeOrder)                              | **POST** /store/order             | Оформить заказ на питомца                                              |
| *UserApi*  | [**createUser**](UserApi.md#createUser)                               | **POST** /user                    | Создать пользователя                                                   |
| *UserApi*  | [**createUsersWithArrayInput**](UserApi.md#createUsersWithArrayInput) | **POST** /user/createWithArray    | Создать список пользователей с использованием массива                  |
| *UserApi*  | [**createUsersWithListInput**](UserApi.md#createUsersWithListInput)   | **POST** /user/createWithList     | Создать список пользователей с использованием списка                   |
| *UserApi*  | [**deleteUser**](UserApi.md#deleteUser)                               | **DELETE** /user/{username}       | Удалить пользователя                                                   |
| *UserApi*  | [**getUserByName**](UserApi.md#getUserByName)                         | **GET** /user/{username}          | Получить пользователя по имени                                         |
| *UserApi*  | [**loginUser**](UserApi.md#loginUser)                                 | **GET** /user/login               | Авторизовать пользователя в системе                                    |
| *UserApi*  | [**logoutUser**](UserApi.md#logoutUser)                               | **GET** /user/logout              | Выйти из текущей сессии                                                |
| *UserApi*  | [**updateUser**](UserApi.md#updateUser)                               | **PUT** /user/{username}          | Обновить пользователя                                                  |

## Документация моделей

- [ApiResponse](model/ApiResponse.md)
- [Category](model/Category.md)
- [Order](model/Order.md)
- [Pet](model/Pet.md)
- [Tag](model/Tag.md)
- [User](model/User.md)

## Документация по авторизации

Определенные схемы авторизации для API:

### api_key

- **Тип**: API ключ
- **Параметр API ключа**: api_key
- **Расположение**: HTTP заголовок

### petstore_auth

- **Тип**: OAuth
- **Поток**: implicit
- **URL авторизации**: https://petstore.swagger.io/oauth/authorize
- **Области**:
    - read:pets: читать ваши питомцы
    - write:pets: изменять питомцев в вашем аккаунте
