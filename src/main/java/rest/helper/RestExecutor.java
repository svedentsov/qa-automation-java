package rest.helper;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.authentication.AuthenticationScheme;
import io.restassured.authentication.NoAuthScheme;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import rest.matcher.condition.Condition;

import java.util.List;
import java.util.Map;

import static common.allure.AllureRestAssuredFilter.withCustomTemplates;

/**
 * Класс для выполнения REST-запросов с использованием библиотеки RestAssured.
 */
@Slf4j
public class RestExecutor {

    private String baseURI = "http://localhost";
    private String proxyHost = null;
    private String basePath = "";
    private int proxyPort = 0;
    private Object body = null;
    private Response response = null;
    private boolean useProxy = false;
    private boolean resetAuth = false;
    private boolean uriDefined = false;
    private boolean pathDefined = false;
    private boolean bodyDefined = false;
    private boolean resetRequest = true;
    private RestAssuredConfig restAssuredConfig = RestAssured.config();
    private AuthenticationScheme authentication = new NoAuthScheme();
    private RequestSpecBuilder requestBuilder = new RequestSpecBuilder();

    /**
     * Конструктор по умолчанию.
     */
    public RestExecutor() {
        appendDefaultCharset(false);
    }

    /**
     * Конструктор с указанием базового URI.
     *
     * @param baseURI базовый URI
     */
    public RestExecutor(String baseURI) {
        setBaseURI(baseURI);
        appendDefaultCharset(false);
    }

    private static void setStaticRequestSpec(RequestSpecification specification) {
        RestAssured.requestSpecification = specification;
    }

    private void setProxy() {
        if (useProxy && proxyHost != null) {
            RestAssured.proxy(proxyHost, proxyPort);
        } else if (useProxy) {
            RestAssured.proxy(proxyPort);
        }
    }

    /**
     * Настройка проверки SSL-сертификатов.
     *
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setRelaxedHTTPSValidation() {
        RestAssured.useRelaxedHTTPSValidation();
        return this;
    }

    /**
     * Устанавливает, сбрасывать ли аутентификацию после выполнения запроса.
     *
     * @param value значение флага сброса аутентификации
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setResetAuth(boolean value) {
        resetAuth = value;
        return this;
    }

    /**
     * Устанавливает, сбрасывать ли параметры запроса после его выполнения.
     *
     * @param value значение флага сброса параметров запроса
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setResetRequest(boolean value) {
        resetRequest = value;
        return this;
    }

    /**
     * Сбрасывает глобальные параметры запроса RestAssured.
     */
    public static void resetRequestSpecification() {
        RestAssured.requestSpecification = null;
    }

    /**
     * Сбрасывает настройки текущего запроса.
     *
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor resetRequest() {
        resetRequestSpecification();
        requestBuilder = new RequestSpecBuilder();
        return this;
    }

    /**
     * Сбрасывает аутентификационные данные.
     *
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor resetAuth() {
        authentication = new NoAuthScheme();
        return this;
    }

    /**
     * Устанавливает следование за редиректами.
     *
     * @param value значение флага следования за редиректами
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor followRedirects(boolean value) {
        restAssuredConfig = restAssuredConfig.redirect(RedirectConfig.redirectConfig().followRedirects(value));
        return this;
    }

    /**
     * Устанавливает добавление кодировки по умолчанию в заголовок Content-Type.
     *
     * @param value значение флага добавления кодировки
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor appendDefaultCharset(boolean value) {
        restAssuredConfig = restAssuredConfig.encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(value));
        return this;
    }

    /**
     * Добавляет параметр запроса.
     *
     * @param paramKey   ключ параметра
     * @param paramValue значение параметра
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addParam(String paramKey, String paramValue) {
        requestBuilder.addParam(paramKey, paramValue);
        return this;
    }

    /**
     * Добавляет параметр формы.
     *
     * @param paramKey   ключ параметра
     * @param paramValue значение параметра
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addFormParam(String paramKey, String paramValue) {
        requestBuilder.addFormParam(paramKey, paramValue);
        return this;
    }

    /**
     * Добавляет параметр пути.
     *
     * @param paramKey   ключ параметра
     * @param paramValue значение параметра
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addPathParam(String paramKey, String paramValue) {
        requestBuilder.addPathParam(paramKey, paramValue);
        return this;
    }

    /**
     * Добавляет параметр запроса.
     *
     * @param paramKey   ключ параметра
     * @param paramValue значение параметра
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addQueryParam(String paramKey, String paramValue) {
        requestBuilder.addQueryParam(paramKey, paramValue);
        return this;
    }

    /**
     * Добавляет параметр запроса с несколькими значениями.
     *
     * @param paramKey    ключ параметра
     * @param paramValues список значений параметра
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addQueryParam(String paramKey, List<String> paramValues) {
        requestBuilder.addQueryParam(paramKey, paramValues.toArray());
        return this;
    }

    /**
     * Добавляет заголовок запроса.
     *
     * @param headerKey   ключ заголовка
     * @param headerValue значение заголовка
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addHeader(String headerKey, String headerValue) {
        requestBuilder.addHeader(headerKey, headerValue);
        return this;
    }

    /**
     * Добавляет cookie.
     *
     * @param cookieName  имя cookie
     * @param cookieValue значение cookie
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addCookie(String cookieName, String cookieValue) {
        requestBuilder.addCookie(cookieName, cookieValue);
        return this;
    }

    /**
     * Добавляет несколько cookies.
     *
     * @param cookies карта cookies
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor addCookies(Map<String, String> cookies) {
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            addCookie(cookie.getKey(), cookie.getValue());
        }
        return this;
    }

    /**
     * Возвращает полный URI текущего запроса.
     *
     * @return строка с полным URI
     */
    public String getURI() {
        String resultUri = "";
        resultUri += uriDefined ? baseURI : RestAssured.baseURI;
        resultUri += pathDefined ? basePath : RestAssured.basePath;
        return resultUri;
    }

    /**
     * Возвращает объект RequestSpecBuilder для настройки запроса.
     *
     * @return объект RequestSpecBuilder
     */
    public RequestSpecBuilder getRequestBuilder() {
        return requestBuilder;
    }

    /**
     * Устанавливает базовый URI.
     *
     * @param baseURI базовый URI
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setBaseURI(String baseURI) {
        this.baseURI = baseURI;
        uriDefined = true;
        return this;
    }

    /**
     * Устанавливает тело запроса.
     *
     * @param body тело запроса
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setBody(Object body) {
        this.body = body;
        bodyDefined = true;
        return this;
    }

    /**
     * Устанавливает базовый путь.
     *
     * @param basePath базовый путь
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setBasePath(String basePath) {
        this.basePath = basePath;
        pathDefined = true;
        return this;
    }

    /**
     * Устанавливает схему аутентификации.
     *
     * @param authentication схема аутентификации
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setAuth(AuthenticationScheme authentication) {
        this.authentication = authentication;
        return this;
    }

    /**
     * Устанавливает тип контента запроса.
     *
     * @param contentType тип контента
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setContentType(ContentType contentType) {
        this.requestBuilder.setContentType(contentType);
        return this;
    }

    /**
     * Устанавливает прокси-сервер по порту.
     *
     * @param port порт прокси-сервера
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setProxy(int port) {
        proxyPort = port;
        return this;
    }

    /**
     * Устанавливает прокси-сервер по хосту и порту.
     *
     * @param host хост прокси-сервера
     * @param port порт прокси-сервера
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setProxy(String host, int port) {
        RestAssured.proxy(host, port);
        return this;
    }

    /**
     * Устанавливает прокси-сервер по спецификации прокси.
     *
     * @param proxySpecification спецификация прокси
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setProxy(ProxySpecification proxySpecification) {
        RestAssured.proxy(proxySpecification);
        return this;
    }

    /**
     * Устанавливает файл для отправки с типом контента и текстом.
     *
     * @param fileName имя файла
     * @param fileType тип файла
     * @param text     текст содержимого файла
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setSendFile(String fileName, String fileType, String text) {
        return setSendFile("file", fileName, fileType, text);
    }

    /**
     * Устанавливает файл для отправки с именем, типом контента и текстом.
     *
     * @param name     имя файла
     * @param fileName имя файла
     * @param fileType тип файла
     * @param text     текст содержимого файла
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor setSendFile(String name, String fileName, String fileType, String text) {
        MultiPartSpecBuilder multipart = new MultiPartSpecBuilder(text)
                .header("Content-Disposition", "form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"")
                .header("Content-Type", fileType);
        requestBuilder.addMultiPart(multipart.build());
        return this;
    }

    /**
     * Выполняет GET-запрос.
     *
     * @return объект Response
     */
    @Step("Выполняется GET запрос")
    public Response get() {
        return get("");
    }

    /**
     * Выполняет GET-запрос по указанному URI.
     *
     * @param uri URI запроса
     * @return объект Response
     */
    @Step("Выполняется GET запрос по URI: {uri}")
    public Response get(String uri) {
        return sendRequest("GET", uri);
    }

    /**
     * Выполняет POST-запрос.
     *
     * @return объект Response
     */
    @Step("Выполняется POST запрос")
    public Response post() {
        return post("");
    }

    /**
     * Выполняет POST-запрос по указанному URI.
     *
     * @param uri URI запроса
     * @return объект Response
     */
    @Step("Выполняется POST запрос по URI: {uri}")
    public Response post(String uri) {
        return sendRequest("POST", uri);
    }

    /**
     * Выполняет PUT-запрос.
     *
     * @return объект Response
     */
    @Step("Выполняется PUT запрос")
    public Response put() {
        return put("");
    }

    /**
     * Выполняет PUT-запрос по указанному URI.
     *
     * @param uri URI запроса
     * @return объект Response
     */
    @Step("Выполняется PUT запрос по URI: {uri}")
    public Response put(String uri) {
        return sendRequest("PUT", uri);
    }

    /**
     * Выполняет DELETE-запрос.
     *
     * @return объект Response
     */
    @Step("Выполняется DELETE запрос")
    public Response delete() {
        return delete("");
    }

    /**
     * Выполняет DELETE-запрос по указанному URI.
     *
     * @param uri URI запроса
     * @return объект Response
     */
    @Step("Выполняется DELETE запрос по URI: {uri}")
    public Response delete(String uri) {
        return sendRequest("DELETE", uri);
    }

    /**
     * Выполняет PATCH-запрос.
     *
     * @return объект Response
     */
    @Step("Выполняется PATCH запрос")
    public Response patch() {
        return patch("");
    }

    /**
     * Выполняет PATCH-запрос по указанному URI.
     *
     * @param uri URI запроса
     * @return объект Response
     */
    @Step("Выполняется PATCH запрос по URI: {uri}")
    public Response patch(String uri) {
        return sendRequest("PATCH", uri);
    }

    /**
     * Отправляет запрос заданного типа по-указанному URI.
     *
     * @param requestType тип запроса (GET, POST, PUT, DELETE, PATCH)
     * @param uri         URI запроса
     * @return объект Response
     */
    private Response sendRequest(String requestType, String uri) {
        if (uriDefined) {
            requestBuilder.setBaseUri(baseURI);
        }

        if (pathDefined) {
            requestBuilder.setBasePath(basePath);
        }

        if (bodyDefined) {
            requestBuilder.setBody(body);
        }

        requestBuilder.setConfig(restAssuredConfig);

        requestBuilder.setAuth(authentication);
        setStaticRequestSpec(requestBuilder.build());

        requestBuilder
                .addFilter(new ResponseLoggingFilter()) // Логирование ответов в консоль
                .addFilter(new RequestLoggingFilter()) // Логирование запросов в консоль
                .addFilter(withCustomTemplates());

        setProxy();

        switch (requestType) {
            case "GET" -> response = RestAssured.get(uri);
            case "POST" -> response = RestAssured.post(uri);
            case "PUT" -> response = RestAssured.put(uri);
            case "DELETE" -> response = RestAssured.delete(uri);
            case "PATCH" -> response = RestAssured.patch(uri);
            default -> throw new IllegalStateException("Unexpected request type: " + requestType);
        }

        if (resetRequest) {
            resetRequest();
        }

        if (resetAuth) {
            resetAuth();
        }

        return response;
    }

    /**
     * Возвращает объект Response последнего запроса.
     *
     * @return объект Response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Возвращает тело ответа последнего запроса в виде объекта указанного класса.
     *
     * @param tClass класс, в который будет преобразовано тело ответа
     * @return объект ответа
     */
    public <T> T getResponseAs(Class<T> tClass) {
        return response.as(tClass);
    }

    /**
     * Возвращает объект по заданному пути JSON из тела ответа.
     *
     * @param jsonPath путь JSON
     * @param tClass   класс, в который будет преобразован объект
     * @return объект по указанному пути
     */
    public <T> T getResponseAs(String jsonPath, Class<T> tClass) {
        return response.jsonPath().getObject(jsonPath, tClass);
    }

    /**
     * Возвращает строку по заданному пути JSON из тела ответа.
     *
     * @param jsonPath путь JSON
     * @return строка по указанному пути
     */
    public String getResponseAs(String jsonPath) {
        return response.jsonPath().getObject(jsonPath, String.class);
    }

    /**
     * Возвращает статусную строку последнего запроса.
     *
     * @return строка статуса
     */
    public String getResponseMessage() {
        return response.getStatusLine();
    }

    /**
     * Возвращает тело ответа последнего запроса в виде объекта JsonPath.
     *
     * @return объект JsonPath
     */
    public JsonPath getResponseAsJson() {
        return response.jsonPath();
    }

    /**
     * Возвращает тело ответа последнего запроса в виде строки
     *
     * @return строка тела ответа
     */
    public String getResponseAsString() {
        return response.asString();
    }

    /**
     * Возвращает тело ответа последнего запроса в виде объекта XmlPath (HTML).
     *
     * @return объект XmlPath
     */
    public XmlPath getResponseAsHtml() {
        return response.htmlPath();
    }

    /**
     * Возвращает тело ответа последнего запроса в виде объекта XmlPath (XML).
     *
     * @return объект XmlPath
     */
    public XmlPath getResponseAsXml() {
        return response.xmlPath();
    }

    /**
     * Возвращает список объектов из тела ответа по указанному пути JSON.
     *
     * @param path   путь JSON в теле ответа
     * @param tClass класс объектов в списке
     * @param <T>    тип объектов в списке
     * @return список объектов указанного типа
     */
    public <T> List<T> getResponseAsList(String path, Class<T> tClass) {
        return response.jsonPath().getList(path, tClass);
    }

    /**
     * Возвращает список объектов из тела ответа.
     *
     * @param tClass класс объектов в списке
     * @param <T>    тип объектов в списке
     * @return список объектов указанного типа
     */
    public <T> List<T> getResponseAsList(Class<T> tClass) {
        return response.jsonPath().getList("$", tClass);
    }

    /**
     * Возвращает тело ответа в виде карты.
     *
     * @return карта значений из тела ответа
     */
    public Map<String, Object> getResponseAsMap() {
        return response.jsonPath().getMap("");
    }

    /**
     * Возвращает тело ответа в виде строки JSON.
     *
     * @return тело ответа в формате JSON
     */
    public String getResponseJsonBody() {
        return response.body().print();
    }

    /**
     * Возвращает все заголовки ответа.
     *
     * @return заголовки ответа
     */
    public Headers getHeaders() {
        return response.getHeaders();
    }

    /**
     * Возвращает значение заголовка по его имени.
     *
     * @param headerName имя заголовка
     * @return значение заголовка
     */
    public String getHeader(String headerName) {
        return response.header(headerName);
    }

    /**
     * Возвращает статусный код ответа.
     *
     * @return статусный код
     */
    public int getStatusCode() {
        return response.getStatusCode();
    }

    /**
     * Возвращает все cookies из ответа.
     *
     * @return карта cookies
     */
    public Map<String, String> getAllCookies() {
        return response.getCookies();
    }

    /**
     * Возвращает значение cookie по его имени.
     *
     * @param name имя cookie
     * @return значение cookie
     */
    public String getCookiesByName(String name) {
        return response.getCookie(name);
    }

    /**
     * Возвращает значение по указанному пути JSON в виде строки.
     *
     * @param jsonPath путь JSON
     * @return строка со значением
     */
    public String getValueLikeString(String jsonPath) {
        return response.jsonPath().getString(jsonPath);
    }

    /**
     * Возвращает значение по указанному HTML пути в виде строки.
     *
     * @param htmlPath путь в HTML
     * @return строка со значением
     */
    public String getHtmlPathValue(String htmlPath) {
        return response.htmlPath().getString(htmlPath).replace("\"", "");
    }

    /**
     * Возвращает значение из тела ответа по указанному пути.
     *
     * @param path путь к значению
     * @return строковое представление значения
     */
    public String getBodyByPath(String path) {
        return response.path(path).toString();
    }

    /**
     * Возвращает значение по указанному пути JSON в виде карты.
     *
     * @param jsonPath путь JSON
     * @return карта со значениями
     */
    public Map<Object, Object> getValueLikeMap(String jsonPath) {
        return response.jsonPath().getMap(jsonPath);
    }

    /**
     * Возвращает объект по указанному пути JSON.
     *
     * @param jsonPath путь JSON
     * @return объект из JSON
     */
    public Object getValueLikeJSON(String jsonPath) {
        return response.jsonPath().getJsonObject(jsonPath);
    }

    /**
     * Возвращает список объектов по указанному пути JSON.
     *
     * @param jsonPath путь JSON
     * @return список объектов
     */
    public List<Object> getValueLikeList(String jsonPath) {
        return response.jsonPath().getList(jsonPath);
    }

    /**
     * Возвращает значение по указанному пути JSON.
     *
     * @param jsonPath путь JSON
     * @param <T>      тип возвращаемого значения
     * @return значение по указанному пути
     */
    public <T> T getJsonPathValue(String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }

    /**
     * Возвращает значение по указанному пути JSON в виде целого числа.
     *
     * @param jsonPath путь JSON
     * @return значение целого числа
     */
    public int getValueLikeInt(String jsonPath) {
        return response.jsonPath().getInt(jsonPath);
    }

    /**
     * Возвращает значение по указанному пути JSON в виде булевого значения.
     *
     * @param jsonPath путь JSON
     * @return булево значение
     */
    public boolean getValueLikeBoolean(String jsonPath) {
        return response.jsonPath().getBoolean(jsonPath);
    }

    /**
     * Проверяет, соответствует ли ответ заданному условию.
     *
     * @param condition условие для проверки
     * @return текущий экземпляр RestExecutor
     */
    public RestExecutor shouldHave(Condition condition) {
        log.info("Проверка условия: " + condition.toString());
        condition.check(response);
        return this;
    }
}
