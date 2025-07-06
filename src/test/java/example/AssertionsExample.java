package example;

import com.svedentsov.db.entity.Address;
import com.svedentsov.db.entity.MyEntity;
import com.svedentsov.db.entity.Role;
import com.svedentsov.matcher.EntityValidator;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

import static com.svedentsov.matcher.PropertyMatcher.value;
import static com.svedentsov.matcher.assertions.BooleanAssertions.isTrue;
import static com.svedentsov.matcher.assertions.CollectionAssertions.*;
import static com.svedentsov.matcher.assertions.InstantAssertions.*;
import static com.svedentsov.matcher.assertions.ListAssertions.*;
import static com.svedentsov.matcher.assertions.LocalDateAssertions.*;
import static com.svedentsov.matcher.assertions.LocalDateTimeAssertions.*;
import static com.svedentsov.matcher.assertions.LocalTimeAssertions.*;
import static com.svedentsov.matcher.assertions.NumberAssertions.*;
import static com.svedentsov.matcher.assertions.PropertyAssertions.*;
import static com.svedentsov.matcher.assertions.StringAssertions.*;

/**
 * Примеры использования всех доступных матчеров данного проекта автоматизированного тестирования.
 */
public class AssertionsExample {

    /**
     * Валидация StringAssertions.
     */
    public void validateStringAssertions(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                value(MyEntity::name, contains("Test")), // Имя содержит "Test"
                value(MyEntity::description, containsIgnoreCase("sample")), // описание содержит "sample" без учета регистра
                value(MyEntity::status, startsWith("Act")), // статус начинается с "Act"
                value(MyEntity::type, startsWithIgnoreCase("usEr")), // тип начинается с "usEr" без учета регистра
                value(MyEntity::email, endsWith(".com")), // email заканчивается на ".com"
                value(MyEntity::role, endsWithIgnoreCase("itor")), // роль заканчивается на "itor" без учета регистра
                value(MyEntity::name, matchesRegex("^[A-Za-z]+$")), // Имя состоит только из букв
                value(MyEntity::name, isNotEmpty()), // Имя не пустое
                value(MyEntity::blankString, isDigitsOnly()), // Содержит только цифры
                value(MyEntity::name, equalTo("TestName")), // Имя равно "TestName"
                value(MyEntity::role, equalsIgnoreCase("editor")), // роль равна "editor" без учета регистра
                value(MyEntity::name, hasLength(8)), // Длина имени равна 8
                value(MyEntity::name, hasMinLength(5)), // Длина имени не меньше 5
                value(MyEntity::name, hasMaxLength(15)), // Длина имени не больше 15
                value(MyEntity::name, hasLengthGreaterThan(7)), // Длина имени больше 7
                value(MyEntity::name, hasLengthLessThan(9)), // Длина имени меньше 9
                value(MyEntity::name, hasLengthBetween(5, 10)), // Длина имени между 5 и 10 включительно
                value(MyEntity::name, hasLengthBetweenExclusive(4, 9)), // Длина имени между 4 и 9 исключительно
                value(MyEntity::blankString, isBlank()), // Строка пустая или состоит из пробелов
                value(MyEntity::name, isNotBlank()), // Строка не пустая и не состоит из пробелов
                value(MyEntity::description, hasNonBlankContent()), // Содержит хотя бы один непробельный символ
                value(MyEntity::role, isAlphabetic()), // роль состоит только из букв
                value(MyEntity::type, isAlphanumeric()), // тип состоит только из букв и цифр
                value(MyEntity::email, isValidEmail()), // email является корректным адресом электронной почты
                value(MyEntity::url, isValidUrl()), // url является корректным URL
                value(MyEntity::status, isUpperCase()), // статус в верхнем регистре
                value(MyEntity::description, isLowerCase()), // описание в нижнем регистре (провалится, если 'A' в начале)
                value(MyEntity::description, startsAndEndsWith("A long")), // описание начинается и заканчивается "A long"
                value(MyEntity::description, hasWordCount(3)), // описание содержит 3 слова
                value(MyEntity::description, hasWord("long")), // описание содержит слово "long"
                value(MyEntity::description, hasWordIgnoreCase("TEXT")), // описание содержит слово "TEXT" без учета регистра
                value(MyEntity::id, matchesPattern(Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[4][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"))), // id соответствует шаблону UUID
                value(MyEntity::name, isPalindrome()), // Имя является палиндромом
                value(MyEntity::jsonString, isXml()), // jsonString является корректным XML
                value(MyEntity::type, containsOnlyCharacters("UserTy123")), // тип содержит только указанные символы
                value(MyEntity::name, doesNotContainCharacters("!@#$")), // Имя не содержит запрещенных символов
                value(MyEntity::blankString, isBinary()), // Строка является двоичным числом
                value(MyEntity::id, isValidUUID()), // id является валидным UUID
                value(MyEntity::id, isValidUuid()), // id является корректным UUID
                value(MyEntity::id, isValidUuidWithoutHyphens()), // id является корректным UUID без дефисов
                value(MyEntity::multiLineText, hasLineCount(3)), // multiLineText содержит 3 строки
                value(MyEntity::description, hasOccurrences("o", 2)), // описание содержит 'o' 2 раза
                value(MyEntity::name, containsDigit()), // Имя содержит хотя бы одну цифру
                value(MyEntity::name, containsLetter()), // Имя содержит хотя бы одну букву
                value(MyEntity::name, containsUpperCase()), // Имя содержит хотя бы один символ в верхнем регистре
                value(MyEntity::name, containsLowerCase()), // Имя содержит хотя бы один символ в нижнем регистре
                value(MyEntity::description, containsWhitespace()), // описание содержит хотя бы один пробельный символ
                value(MyEntity::name, containsNoWhitespace()), // Имя не содержит пробельных символов
                value(MyEntity::phone, isValidPhoneNumber()), // phone является корректным номером телефона
                value(MyEntity::ipAddress, isValidIpAddress()), // ipAddress является корректным IP-адресом
                value(MyEntity::macAddress, isValidMacAddress()), // macAddress является корректным MAC-адресом
                value(MyEntity::eventTime, isValidTime()), // eventTime является корректным временем
                value(MyEntity::type, containsOnly("UserTy123")), // тип содержит только символы из "UserTy123"
                value(MyEntity::hexColor, isValidHexColor()), // hexColor является корректным HEX-цветом
                value(MyEntity::name, hasCharacterCount('T', 1)), // Имя содержит символ 'T' 1 раз
                value(MyEntity::name, startsWithDigit()), // Имя начинается с цифры
                value(MyEntity::name, endsWithDigit()), // Имя заканчивается цифрой
                value(MyEntity::name, startsWithLetter()), // Имя начинается с буквы
                value(MyEntity::name, endsWithLetter()), // Имя заканчивается буквой
                value(MyEntity::description, containsDigits()), // описание содержит последовательность цифр
                value(MyEntity::name, containsLetters()), // Имя содержит последовательность букв
                value(MyEntity::isTrueString, isBoolean()), // Строка представляет собой логическое значение (true или false, без учёта регистра)
                value(MyEntity::uniqueCharsString, containsOnlyUniqueCharacters()), // Содержит только уникальные символы
                value(MyEntity::name, isString()), // Значение является строкой
                value(MyEntity::id, isNotNull()), // Значение не равно null
                value(MyEntity::name, isNonBlank()), // Строка не пуста и не состоит только из пробелов
                value(MyEntity::name, lengthEqualTo(8)), // Длина строки равна 8
                value(MyEntity::name, lengthGreaterThan(7)), // Длина строки больше 7
                value(MyEntity::name, lengthLessThan(9)), // Длина строки меньше 9
                value(MyEntity::name, lengthBetweenStr(5, 10)), // Длина строки в диапазоне [5, 10]
                value(MyEntity::name, maxSize(10)), // Строка не превышает 10 символов
                value(MyEntity::name, minSize(5)), // Строка не короче 5 символов
                value(MyEntity::multiLineText, maxLines(3)), // Строка не превышает 3 строк
                value(MyEntity::name, doesNotContain("xyz")), // Не содержит "xyz"
                value(MyEntity::name, containsIgnoringCase("NAME")), // Содержит "NAME" без учета регистра
                value(MyEntity::description, containsAny("sample", "example")), // Содержит хотя бы одну из подстрок
                value(MyEntity::description, containsAll("A", "long", "text")), // Содержит все подстроки
                value(MyEntity::description, containsNone("bad", "wrong")), // Не содержит ни одной из подстрок
                value(MyEntity::pangramString, containsAllVowels()), // Содержит все гласные буквы
                value(MyEntity::description, wordsOrder("A", "long", "text")), // Содержит слова в указанном порядке
                value(MyEntity::description, wordsReverseOrder("text", "long", "A")), // Содержит слова в обратном порядке
                value(MyEntity::name, startsAndEndsWith("Test", "Name")), // Строка начинается и заканчивается заданным префиксом и суффиксом
                value(MyEntity::name, startsWithAndEndsWithIgnoreCase("test", "name")), // Строка начинается и заканчивается заданным префиксом/суффиксом, игнорируя регистр
                value(MyEntity::mixedCaseString, startsWithLetterEndsWithDigit()), // Строка начинается с буквы и заканчивается цифрой
                value(MyEntity::numericString, startsAndEndsWithDigit()), // Строка начинается и заканчивается цифрой
                value(MyEntity::email, matchesCaseInsensitive("^TEST@EXAMPLE.COM$")), // Строка соответствует регулярному выражению без учета регистра
                value(MyEntity::wildcardString, matchesWildcardPattern("Test*Pattern?")), // Строка соответствует шаблону Wildcard
                value(MyEntity::description, containsOccurrences("e", 2)), // Содержит "e" 2 раза
                value(MyEntity::repeatedSubstring, containsRepeatedSubstring("ab", 3)), // Содержит подстроку, повторенную 3 раза подряд
                value(MyEntity::description, containsAllSubstringsUnorderedNoOverlap("long", "text")), // Содержит заданные подстроки в произвольном порядке и без перекрытий
                value(MyEntity::description, containsSubstringExactCount("e", 2)), // Содержит подстроку "e" ровно 2 раза
                value(MyEntity::base64String, isBase64Encoded()), // Строка является закодированной Base64
                value(MyEntity::mixedCaseString, containsLetterAndDigit()), // Содержит хотя бы одну цифру и одну букву
                value(MyEntity::name, containsNoDigits()), // Имя не содержит цифр
                value(MyEntity::name, isLatinLettersOnly()), // Имя содержит только латинские буквы
                value(MyEntity::specialCharString, containsSpecialCharacter()), // Содержит хотя бы один специальный символ
                value(MyEntity::numericString, hasDigits(5)), // Содержит 5 цифр
                value(MyEntity::mixedCaseString, hasUpperCaseLetters(2)), // Содержит 2 заглавные буквы
                value(MyEntity::mixedCaseString, hasLowerCaseLetters(3)), // Содержит 3 строчные буквы
                value(MyEntity::specialCharString, hasSpecialCharacters(1)), // Содержит 1 специальный символ
                value(MyEntity::uniqueCharsString, hasAllUniqueCharacters()), // Содержит только уникальные символы
                value(MyEntity::bigramString, hasUniqueBigrams()), // Содержит только уникальные биграммы
                value(MyEntity::trigramString, hasUniqueTrigrams()), // Содержит только уникальные триграммы
                value(MyEntity::uniqueWordsString, allWordsUnique()), // Все слова в строке уникальны
                value(MyEntity::uniqueWordsIgnoreCaseString, hasUniqueWordsIgnoreCase()), // Содержит только уникальные слова независимо от регистра
                value(MyEntity::pangramString, isPangram()), // Строка является панграммой
                value(MyEntity::eventTime, matchesTimeFormat("HH:mm:ss")), // Строка соответствует формату времени
                value(MyEntity::name, isUtf8Encoded()), // Строка закодирована в UTF-8
                value(MyEntity::name, isAscii()), // Строка имеет кодировку ASCII
                value(MyEntity::name, isEncodableIn("UTF-8")), // Строка кодируема в UTF-8
                value(MyEntity::url, isValidURL()), // Строка является валидным URL
                value(MyEntity::jsonString, isValidJson()), // Строка является валидным JSON
                value(MyEntity::xmlString, isValidXml()), // Строка является валидным XML
                value(MyEntity::javaIdentifier, isValidJavaIdentifier()), // Строка является валидным идентификатором Java
                value(MyEntity::noTabsString, hasNoTabs()), // Не содержит символов табуляции
                value(MyEntity::noControlCharsString, hasNoControlCharacters()), // Не содержит символов управления
                value(MyEntity::noRepeatedSequencesString, hasNoRepeatedSequences(2)), // Не содержит повторяющихся последовательностей длины 2
                value(MyEntity::noConsecutiveSpacesString, hasNoConsecutiveSpaces()), // Не содержит подряд идущих пробелов
                value(MyEntity::noConsecutiveDuplicateCharsString, hasNoConsecutiveDuplicateCharacters()), // Не содержит подряд идущих одинаковых символов
                value(MyEntity::noConsecutiveLetterDigitString, hasNoConsecutiveLettersAndDigits()), // Не содержит подряд идущих букв и цифр
                value(MyEntity::name, containsNoForbiddenChars("!@#")), // Имя не содержит запрещенных символов
                value(MyEntity::description, doesNotContainSubstringsStartingAndEndingWith('<', '>')), // Не содержит подстрок, начинающихся с '<' и заканчивающихся на '>'
                value(MyEntity::allowedCharsString, containsOnlyAllowedChars("abc123")), // Содержит только символы из заданного набора
                value(MyEntity::allowedSpecialCharsString, containsOnlyAllowedSpecialChars("!@#$")), // Не содержит специальных символов, кроме разрешенных "!@#$"
                value(MyEntity::asciiString, hasMaxCharCode(127)), // Не содержит символов с кодом выше 127 (ASCII)
                value(MyEntity::greekString, containsOnlyUnicodeBlock("GREEK")), // Содержит только символы из Unicode блока "GREEK"
                value(MyEntity::sentence, containsWholeWord("quick")), // Содержит слово "quick" с учетом границ слова
                value(MyEntity::sentence, doesNotContainWholeWord("fast")), // Не содержит слово "fast" с учетом границ слова
                value(MyEntity::mixedUnicodeString, hasUnicodeCharactersInRange(0x0400, 0x04FF, 3)), // Содержит 3 символа Unicode из кириллического диапазона
                value(MyEntity::mixedUnicodeString, containsOnlyUnicodeBlocks("LATIN", "CYRILLIC")), // Содержит только символы из Unicode блоков "LATIN" и "CYRILLIC"
                value(MyEntity::longSequenceString, hasNoLongSequencesOfSameCharacter(2)), // Не содержит последовательностей одинаковых символов длиной более 2
                value(MyEntity::alphanumericString, containsOnlyRegex("[a-zA-Z0-9]")), // Содержит только символы, соответствующие регулярному выражению "[a-zA-Z0-9]"
                value(MyEntity::mixedCaseString, hasSpecificCharCount("aeiouAEIOU", 5)), // Содержит 5 символов из набора гласных ("aeiouAEIOU")
                value(MyEntity::sentence, hasSpaceCount(4)), // Содержит 4 пробела
                value(MyEntity::multipleSpacesString, hasMaxConsecutiveSpaces(2)), // Не содержит более 2 подряд идущих пробелов
                value(MyEntity::passwordString, hasCategorizedCharCounts(6, 2, 0, 1)), // Содержит 6 букв, 2 цифры, 0 пробелов и 1 специальный символ
                value(MyEntity::passwordString, containsMinimumCharacterTypes(5, 1, 0, 1)), // Содержит минимум 5 букв, 1 цифру, 0 пробелов и 1 специальный символ
                value(MyEntity::sentence, startsWithCapitalLetter()), // Строка начинается с заглавной буквы
                value(MyEntity::mixedCaseBalanceString, hasEqualUpperAndLowerCase()), // Содержит одинаковое количество заглавных и строчных букв
                value(MyEntity::exactCaseString, hasExactCaseCounts(3, 3)), // Содержит ровно 3 заглавных и 3 строчных буквы
                value(MyEntity::consecutiveCaseString, hasMaxConsecutiveSameCaseLetters(2)), // Не содержит более 2 подряд идущих букв одного регистра
                value(MyEntity::sentence, endsWithPunctuation()), // Строка заканчивается точкой, восклицательным или вопросительным знаком
                value(MyEntity::distinctCharsString, hasDistinctCharacterCount(5)), // Содержит 5 различных символов
                value(MyEntity::complexPasswordString, hasMultipleCharacterSetsCounts(new String[]{"abc", "123", "!@#"}, new int[]{3, 2, 1})), // Содержит 3 символа из "abc", 2 из "123" и 1 из "!@#"
                value(MyEntity::fixedPatternString, hasFixedLengthAndMatchesPattern(6, "\\d{3}[A-Z]{3}")) // Строка имеет длину 6 и соответствует шаблону "3 цифры + 3 заглавные буквы"
        );
    }

    /**
     * Валидация NumberAssertions.
     */
    public void validateNumberAssertions(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                value(MyEntity::age, numberEqualTo(25)), // возраст равен 25
                value(MyEntity::age, numberNotEqualTo(30)), // возраст не равен 30
                value(MyEntity::age, numberBetweenExclusive(20, 30)), // возраст находится строго между 20 и 30 (не включая 20 и 30)
                value(MyEntity::age, numberInRange(20, 30)), // возраст находится в диапазоне [20, 30]
                value(MyEntity::age, numberGreaterThan(20)), // возраст больше 20
                value(MyEntity::age, numberLessThan(30)), // возраст меньше 30
                value(MyEntity::age, numberGreaterThanOrEqualTo(25)), // возраст больше или равен 25
                value(MyEntity::age, numberLessThanOrEqualTo(25)), // возраст меньше или равен 25
                value(MyEntity::score, numberBetween(BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0))), // счет находится между 10.0 и 20.0 (включительно)
                value(MyEntity::score, numberStrictlyBetween(BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0))), // счет находится строго между 10.0 и 20.0
                value(MyEntity::age, numberIsZero()), // возраст равен 0
                value(MyEntity::age, numberIsNotZero()), // возраст не равен 0
                value(MyEntity::age, numberIsPositive()), // возраст положительный
                value(MyEntity::age, numberIsNegative()), // возраст отрицательный
                value(MyEntity::age, numberIsNonNegative()), // возраст неотрицательный (>= 0)
                value(MyEntity::age, numberIsNonPositive()), // возраст неположительный (<= 0)
                value(MyEntity::score, numberApproximatelyEqualTo(BigDecimal.valueOf(100.0), BigDecimal.valueOf(5.0))), // счет примерно равен 100.0 с допуском 5.0
                value(MyEntity::score, numberApproximatelyZero(BigDecimal.valueOf(0.1))), // счет примерно равен 0 с допуском 0.1
                value(MyEntity::score, numberIsInteger()), // счет является целым числом
                value(MyEntity::age, numberIsEven()), // возраст четный
                value(MyEntity::age, numberIsOdd()), // возраст нечетный
                value(MyEntity::score, numberIsDivisibleBy(BigDecimal.valueOf(5))), // счет делится на 5 без остатка
                value(MyEntity::score, numberWithinPercentage(BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0))), // счет в пределах 10% от 100.0
                value(MyEntity::age, numberHasSameSignAs(BigDecimal.valueOf(-10))), // возраст имеет тот же знак, что и -10
                value(MyEntity::age, numberIsPrime()), // возраст является простым числом (требует целого числа)
                value(MyEntity::age, numberIsPerfectSquare()), // возраст является совершенным квадратом (требует целого числа)
                value(MyEntity::score, numberHasFractionalPart()), // счет имеет дробную часть
                value(MyEntity::score, numberHasScale(2)), // масштаб счета равен 2
                value(MyEntity::age, numberLeftInclusiveRightExclusive(18, 65)), // возраст в диапазоне [18, 65)
                value(MyEntity::age, numberLeftExclusiveRightInclusive(18, 65)), // возраст в диапазоне (18, 65]
                value(MyEntity::age, numberIsCloseTo(25, 1)), // возраст близок к 25 с отклонением 1
                value(MyEntity::score, numberIsFinite()), // счет является конечным числом (не NaN, не бесконечность)
                value(MyEntity::score, numberIsNaN()), // пример для NaN
                value(MyEntity::score, numberIsInfinite()), // пример для бесконечности
                value(MyEntity::score, numberHasAbsoluteValueGreaterThan(BigDecimal.valueOf(10))), // абсолютное значение возраста больше 10
                value(MyEntity::score, numberHasAbsoluteValueLessThan(BigDecimal.valueOf(30))), // абсолютное значение возраста меньше 30
                value(MyEntity::score, numberHasAbsoluteValueBetween(BigDecimal.valueOf(10), BigDecimal.valueOf(30))), // абсолютное значение возраста между 10 и 30
                value(MyEntity::score, numberApproximatelyEqualRelative(BigDecimal.valueOf(100.0), BigDecimal.valueOf(0.01))), // счет примерно равен 100.0 с относительной погрешностью 0.01 (1%)
                value(MyEntity::score, numberIsBetweenZeroAndOne()), // счет находится между 0 и 1 (включительно)
                value(MyEntity::age, numberFitsArithmeticSequence(10, 5, 30)), // возраст в арифметической прогрессии, начиная с 10, с шагом 5, до 30
                value(MyEntity::age, numberHasNumberOfDigits(2)), // возраст имеет 2 цифры в целой части
                value(MyEntity::age, numberIsNumber()), // возраст является числом (экземпляром Number)
                value(MyEntity::age, numberGreaterOrEqualTo(25)), // возраст больше или равен 25
                value(MyEntity::age, numberLessOrEqualTo(25)), // возраст меньше или равен 25
                value(MyEntity::age, numberIsPositiveOrZero()), // возраст больше или равен 0
                value(MyEntity::age, numberIsNegativeOrZero()), // возраст меньше или равен 0
                value(MyEntity::age, numberMultipleOf(5)), // возраст кратен 5
                value(MyEntity::age, numberIsIntegerType()), // возраст является Integer
                value(MyEntity::age, numberIsLongType()), // возраст является Long
                value(MyEntity::score, numberIsFloatType()), // для Float
                value(MyEntity::score, numberIsBigDecimalType()), // счет является BigDecimal
                value(MyEntity::age, numberIsBigIntegerType()), // возраст является BigInteger
                value(MyEntity::age, numberIsShortType()), // возраст является Short
                value(MyEntity::age, numberIsByteType()), // возраст является Byte
                value(MyEntity::age, numberIsOne()), // возраст равен 1
                value(MyEntity::age, numberIsMinusOne()), // возраст равен -1
                value(MyEntity::score, numberIsPositiveInfinity()), // положительная бесконечности
                value(MyEntity::score, numberIsNegativeInfinity()), // отрицательная бесконечности
                value(MyEntity::age, numberIsStrictlyPositive()), // возраст строго положительный (> 0)
                value(MyEntity::age, numberIsStrictlyNegative()), // возраст строго отрицательный (< 0)
                value(MyEntity::age, numberIsPowerOfTwo()), // возраст является степенью двойки (требует целого числа)
                value(MyEntity::age, numberIsEvenAndPositive()), // возраст четный и положительный
                value(MyEntity::age, numberIsOddAndPositive()), // возраст нечетный и положительный
                value(MyEntity::age, numberIsEvenAndNegative()), // возраст четный и отрицательный
                value(MyEntity::age, numberIsOddAndNegative()), // возраст нечетный и отрицательный
                value(MyEntity::age, numberIsPositiveAndMultipleOf(5)), // возраст положительный и кратный 5
                value(MyEntity::age, numberIsNegativeAndMultipleOf(5)), // возраст отрицательный и кратный 5
                value(MyEntity::age, numberIsGreaterThanAndLessThanOrEqualTo(20, 25)), // возраст > 20 и <= 25
                value(MyEntity::age, numberIsGreaterThanOrEqualToAndLessThan(25, 30)), // возраст >= 25 и < 30
                value(MyEntity::score, numberIsCloseToZero()), // счет близок к нулю
                value(MyEntity::age, numberIsDivisorOf(50)), // возраст является делителем числа 50
                value(MyEntity::age, numberIsEqualToAbsoluteValue(25)), // абсолютное значение возраста равно 25
                value(MyEntity::age, numberIsGreaterThanAbsoluteValue(20)), // абсолютное значение возраста больше 20
                value(MyEntity::age, numberIsLessThanAbsoluteValue(30)), // абсолютное значение возраста меньше 30
                value(MyEntity::score, numberIsFloatRangeStartInclusiveEndExclusive(15.0, 16.0)), // счет в диапазоне [15.0, 16.0)
                value(MyEntity::score, numberIsFloatRangeStartExclusiveEndInclusive(15.0, 16.0)), // счет в диапазоне (15.0, 16.0]
                value(MyEntity::age, numberIsStrictlyPositiveInteger()), // возраст является строго положительным целым числом
                value(MyEntity::age, numberIsStrictlyNegativeInteger()), // возраст является строго отрицательным целым числом
                value(MyEntity::age, numberIsZeroInteger()), // возраст является целым числом и равен 0
                value(MyEntity::age, numberIsOneInteger()), // возраст является целым числом и равен 1
                value(MyEntity::age, numberIsOddAndNonNegative()), // возраст нечетный и неотрицательный
                value(MyEntity::age, numberIsEvenAndNonNegative()), // возраст четный и неотрицательный
                value(MyEntity::age, numberIsOddAndNonPositive()), // возраст нечетный и неположительный
                value(MyEntity::age, numberIsEvenAndNonPositive()), // возраст четный и неположительный
                value(MyEntity::age, numberIsInstanceOf(Integer.class)), // возраст является экземпляром Integer
                value(MyEntity::score, numberHasPrecision(4)), // точность числа score равна 4
                value(MyEntity::age, numberGreaterOrEqualTo(25.0)), // возраст больше или равен 25.0
                value(MyEntity::age, numberLessOrEqualTo(25.0)), // возраст меньше или равен 25.0
                value(MyEntity::age, numberGreaterThan(20.0)), // возраст больше 20.0
                value(MyEntity::age, numberLessThan(30.0)), // возраст меньше 30.0
                value(MyEntity::age, numberEqualTo(25.0)), // возраст равен 25.0
                value(MyEntity::age, numberNotEqualTo(26.0)), // возраст не равен 26.0
                value(MyEntity::age, numberInRange(20.0, 30.0)), // возраст в диапазоне [20.0, 30.0]
                value(MyEntity::age, numberBetweenExclusive(20.0, 30.0)) // возраст в диапазоне (20.0, 30.0)
        );
    }

    /**
     * Валидация InstantAssertions
     */
    public void validateInstantAssertions(MyEntity entity) {
        Instant now = Instant.now();
        Instant fiveMinutesAgo = now.minus(5, ChronoUnit.MINUTES);
        Instant fiveMinutesLater = now.plus(5, ChronoUnit.MINUTES);

        EntityValidator.of(entity).shouldHave(
                // Общие проверки на до/после/в диапазоне/равенство
                value(MyEntity::createdAt, instantBefore(now.plusSeconds(10))), // createdAt должен быть до 10 секунд от сейчас
                value(MyEntity::updatedAt, instantAfter(fiveMinutesAgo)), // updatedAt должен быть после 5 минут назад
                value(MyEntity::lastLogin, instantInRange(fiveMinutesAgo, fiveMinutesLater)), // lastLogin должен быть в диапазоне
                value(MyEntity::createdAt, instantEqualsTo(entity.createdAt())), // createdAt должен быть равен самому себе
                value(MyEntity::createdAt, instantNotEqualsTo(now.plusSeconds(1))), // createdAt не должен быть равен сейчас + 1 секунда
                value(MyEntity::createdAt, instantAfterOrEqualTo(fiveMinutesAgo)), // createdAt должен быть после или равен 5 минут назад
                value(MyEntity::createdAt, instantBeforeOrEqualTo(fiveMinutesLater)), // createdAt должен быть до или равен 5 минут вперед
                // Проверки на прошлое/будущее и удаленность от текущего момента
                value(MyEntity::createdAt, instantIsInPast()), // createdAt должен быть в прошлом
                value(MyEntity::futureEventTime, instantIsInFuture()), // futureEventTime должен быть в будущем
                value(MyEntity::lastLogin, instantWithin(now, Duration.ofMinutes(10))), // lastLogin должен быть в пределах 10 минут от сейчас
                value(MyEntity::lastLogin, instantWithin(now, 10, ChronoUnit.MINUTES)), // lastLogin должен быть в пределах 10 минут от сейчас
                value(MyEntity::createdAt, instantNotOlderThan(Duration.ofDays(365))), // createdAt не старше 365 дней
                value(MyEntity::futureEventTime, instantNotFurtherThan(Duration.ofDays(365))), // futureEventTime не дальше 365 дней вперед
                // Проверки по дате и времени
                value(MyEntity::createdAt, instantSameDateAs(now)), // createdAt должен быть в тот же день, что и сейчас
                value(MyEntity::createdAt, instantInYear(2025)), // createdAt должен быть в 2025 году
                value(MyEntity::createdAt, instantInMonth(6)), // createdAt должен быть в июне
                value(MyEntity::createdAt, instantInMonth(Month.JUNE)), // createdAt должен быть в июне
                value(MyEntity::createdAt, instantDayOfWeek(DayOfWeek.SATURDAY)), // createdAt должен быть в субботу
                value(MyEntity::createdAt, instantInAnyOfDaysOfWeek(Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))), // createdAt должен быть в выходной
                value(MyEntity::createdAt, instantIsWeekend()), // createdAt должен быть выходным
                value(MyEntity::createdAt, instantIsWeekday()), // createdAt должен быть рабочим днем
                value(MyEntity::createdAt, instantIsStartOfDay()), // createdAt должен быть началом дня
                value(MyEntity::createdAt, instantIsEndOfDay()), // createdAt должен быть концом дня
                value(MyEntity::createdAt, instantIsStartOfHour()), // createdAt должен быть началом часа
                value(MyEntity::createdAt, instantIsEndOfHour()), // createdAt должен быть концом часа
                value(MyEntity::createdAt, instantIsStartOfMinute()), // createdAt должен быть началом минуты
                value(MyEntity::createdAt, instantIsEndOfMinute()), // createdAt должен быть концом минуты
                value(MyEntity::createdAt, instantIsStartOfSecond()), // createdAt должен быть началом секунды
                value(MyEntity::createdAt, instantIsEndOfSecond()), // createdAt должен быть концом секунды
                // Проверки близости по временным единицам
                value(MyEntity::lastLogin, instantWithinSecondsOf(now, 600)), // lastLogin в пределах 600 секунд от сейчас
                value(MyEntity::lastLogin, instantWithinMinutesOf(now, 10)), // lastLogin в пределах 10 минут от сейчас
                value(MyEntity::lastLogin, instantWithinHoursOf(now, 1)), // lastLogin в пределах 1 часа от сейчас
                value(MyEntity::lastLogin, instantWithinDaysOf(now, 1)), // lastLogin в пределах 1 дня от сейчас
                // Проверки по полям ChronoField
                value(MyEntity::createdAt, instantCheckField(ChronoField.YEAR, 2025)), // год createdAt равен 2025
                value(MyEntity::createdAt, instantIsFieldGreaterThan(ChronoField.HOUR_OF_DAY, 5)), // час createdAt больше 5
                value(MyEntity::createdAt, instantIsFieldLessThan(ChronoField.HOUR_OF_DAY, 23)), // час createdAt меньше 23
                value(MyEntity::createdAt, instantIsFieldGreaterOrEqual(ChronoField.DAY_OF_MONTH, 1)), // день месяца createdAt >= 1
                value(MyEntity::createdAt, instantIsFieldLessOrEqual(ChronoField.DAY_OF_MONTH, 31)), // день месяца createdAt <= 31
                // Упрощенные проверки по полям
                value(MyEntity::createdAt, instantInHour(12)), // час createdAt равен 12
                value(MyEntity::createdAt, instantInMinute(30)), // минута createdAt равна 30
                value(MyEntity::createdAt, instantInSecond(0)), // секунда createdAt равна 0
                value(MyEntity::createdAt, instantInMillisecond(0)), // миллисекунда createdAt равна 0
                value(MyEntity::createdAt, instantDayOfMonth(8)), // день месяца createdAt равен 8
                value(MyEntity::createdAt, instantIsFirstDayOfMonth()), // createdAt - первый день месяца
                value(MyEntity::createdAt, instantIsLastDayOfMonth()), // createdAt - последний день месяца
                value(MyEntity::createdAt, instantDayOfYear(159)), // createdAt - 159-й день года (для 08.06.2025)
                value(MyEntity::createdAt, instantInQuarter(2)), // createdAt во втором квартале
                // Проверки на части дня
                value(MyEntity::createdAt, instantIsMorning()), // createdAt утром (6-11)
                value(MyEntity::createdAt, instantIsAfternoon()), // createdAt днем (12-17)
                value(MyEntity::createdAt, instantIsEvening()), // createdAt вечером (18-22)
                // Проверки на временную зону
                value(MyEntity::createdAt, instantIsInZone(ZoneId.systemDefault())), // createdAt в системной временной зоне
                // Проверки на конкретные даты и время
                value(MyEntity::createdAt, instantIsDate(6, 8)), // createdAt - 8 июня
                value(MyEntity::createdAt, instantIsDate(2025, 6, 8)), // createdAt - 8 июня 2025
                value(MyEntity::createdAt, instantIsTime(12, 30)), // время createdAt - 12:30
                value(MyEntity::createdAt, instantIsTime(12, 30, 0)), // время createdAt - 12:30:00
                value(MyEntity::createdAt, instantIsTime(12, 30, 0, 0)), // время createdAt - 12:30:00.000
                value(MyEntity::createdAt, instantIsLocalDateTime(LocalDateTime.ofInstant(entity.createdAt(), ZoneId.systemDefault()))), // createdAt соответствует своему LocalDateTime
                value(MyEntity::createdAt, instantIsZonedDateTime(ZonedDateTime.ofInstant(entity.createdAt(), ZoneId.systemDefault()))), // createdAt соответствует своему ZonedDateTime
                value(MyEntity::createdAt, instantIsOffsetDateTime(OffsetDateTime.ofInstant(entity.createdAt(), ZoneOffset.ofHours(2)))), // createdAt соответствует своему OffsetDateTime (для CEST)
                // Проверки на совпадение частей времени
                value(MyEntity::createdAt, instantSameHourAs(now)), // час createdAt совпадает с часом сейчас
                value(MyEntity::createdAt, instantSameMinuteAs(now)), // минута createdAt совпадает с минутой сейчас
                value(MyEntity::createdAt, instantSameSecondAs(now)), // секунда createdAt совпадает с секундой сейчас
                value(MyEntity::createdAt, instantSameMillisecondAs(now)), // миллисекунда createdAt совпадает с миллисекундой сейчас
                // Проверки на начало/конец недели/месяца/года
                value(MyEntity::createdAt, instantIsStartOfWeek()), // createdAt - начало недели (понедельник 00:00:00)
                value(MyEntity::createdAt, instantIsEndOfWeek()), // createdAt - конец недели (воскресенье 23:59:59.999999999)
                value(MyEntity::createdAt, instantIsStartOfMonth()), // createdAt - начало месяца
                value(MyEntity::createdAt, instantIsEndOfMonth()), // createdAt - конец месяца
                value(MyEntity::createdAt, instantIsStartOfYear()), // createdAt - начало года
                value(MyEntity::createdAt, instantIsEndOfYear()), // createdAt - конец года
                // Проверки на полдень/полночь
                value(MyEntity::createdAt, instantIsNoon()), // createdAt - полдень (12:00:00)
                value(MyEntity::createdAt, instantIsMidnight()), // createdAt - полночь (00:00:00)
                // Упрощенные проверки на "в пределах последних/следующих"
                value(MyEntity::createdAt, instantIsWithinLastSeconds(10)), // createdAt в пределах последних 10 секунд
                value(MyEntity::futureEventTime, instantIsWithinNextSeconds(60)), // futureEventTime в пределах следующих 60 секунд
                value(MyEntity::createdAt, instantIsWithinLastMinutes(1)), // createdAt в пределах последних 1 минуты
                value(MyEntity::futureEventTime, instantIsWithinNextMinutes(1)), // futureEventTime в пределах следующих 1 минуты
                value(MyEntity::createdAt, instantIsWithinLastHours(1)), // createdAt в пределах последних 1 часа
                value(MyEntity::futureEventTime, instantIsWithinNextHours(1)), // futureEventTime в пределах следующих 1 часа
                value(MyEntity::createdAt, instantIsWithinLastDays(1)), // createdAt в пределах последних 1 дня
                value(MyEntity::futureEventTime, instantIsWithinNextDays(1)) // futureEventTime в пределах следующих 1 дня
        );
    }

    /**
     * Валидация LocalTimeAssertions.
     */
    public void validateLocalTimeAssertions(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                // Основные сравнения
                value(MyEntity::openingTime, timeBefore(LocalTime.of(15, 0))), // Время события до 15:00
                value(MyEntity::openingTime, timeAfter(LocalTime.of(14, 0))), // Время события после 14:00
                value(MyEntity::openingTime, timeEquals(LocalTime.of(9, 0))), // Время открытия ровно 09:00
                value(MyEntity::openingTime, timeAfterOrEqual(LocalTime.of(18, 0))), // Время закрытия в 18:00 или позже
                value(MyEntity::openingTime, timeBeforeOrEqual(LocalTime.of(9, 0))), // Время открытия в 09:00 или раньше
                // Диапазоны
                value(MyEntity::openingTime, timeIsBetween(LocalTime.of(14, 0), LocalTime.of(15, 0))), // Время события между 14:00 и 15:00 (включительно)
                value(MyEntity::openingTime, timeIsStrictlyBetween(LocalTime.of(14, 20), LocalTime.of(14, 40))), // Время события строго между 14:20 и 14:40
                // Компоненты времени
                value(MyEntity::openingTime, timeHasHour(14)), // Час времени события равен 14
                value(MyEntity::openingTime, timeHasMinute(30)), // Минута времени события равна 30
                value(MyEntity::openingTime, timeHasSecond(15)), // Секунда времени события равна 15
                value(MyEntity::openingTime, timeHasNano(0)), // Наносекунды времени события равны 0
                // Сравнения с игнорированием
                value(MyEntity::openingTime, timeIsEqualToIgnoringNanos(LocalTime.of(9, 0, 0, 123))), // Равно 09:00, игнорируя наносекунды
                value(MyEntity::openingTime, timeIsEqualToIgnoringSeconds(LocalTime.of(9, 0, 55))), // Равно 09:00, игнорируя секунды
                // Периоды дня
                value(MyEntity::openingTime, timeIsMorning()), // Время 10:45 - это утро
                value(MyEntity::openingTime, timeIsAfternoon()), // Время 14:30 - это день
                value(MyEntity::openingTime, timeIsEvening()), // Время 20:05 - это вечер
                value(MyEntity::openingTime, timeIsNight()), // Время 23:00 - это ночь
                // Конкретные случаи
                value(MyEntity::openingTime, timeIsOfficeHours(LocalTime.of(9, 0), LocalTime.of(18, 0))), // Время события попадает в рабочие часы 09-18
                value(MyEntity::openingTime, timeIsMidnight()), // Проверка на полночь
                value(MyEntity::openingTime, timeIsNoon()), // Проверка на полдень
                value(MyEntity::openingTime, timeIsAm()), // Время до полудня
                value(MyEntity::openingTime, timeIsPm()), // Время после полудня
                // Продвинутые проверки
                value(MyEntity::openingTime, timeHasFieldValue(ChronoField.HOUR_OF_AMPM, 8)), // В 20:05 (PM) час в 12-часовом формате равен 8
                value(MyEntity::openingTime, timeIsWithinHour(14)), // Время 14:30 находится внутри 14-го часа
                value(MyEntity::openingTime, timeIsWithinMinute(14, 30)), // Время 14:30:15 находится внутри минуты 14:30
                value(MyEntity::openingTime, timeIsTruncatedTo(ChronoUnit.HOURS)), // Время 09:00:00 усечено до часов (минуты, секунды, нано равны 0)
                value(MyEntity::openingTime, timeIsTruncatedTo(ChronoUnit.MINUTES)) // Время 09:00:00 усечено до минут (секунды, нано равны 0)
        );
    }

    /**
     * Валидация LocalDateAssertions.
     */
    public void validateLocalDateAssertions(MyEntity entity) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        LocalDate dateInFuture = today.plusYears(1).plusMonths(2).plusDays(3);
        LocalDate dateInPast = today.minusYears(1).minusMonths(2).minusDays(3);
        LocalDate leapYearDate = LocalDate.of(2024, 2, 29);
        LocalDate nonLeapYearDate = LocalDate.of(2023, 2, 28);
        LocalDate firstDayOfMonth = LocalDate.of(2025, 1, 1);
        LocalDate lastDayOfMonth = LocalDate.of(2025, 1, 31);
        LocalDate firstDayOfYear = LocalDate.of(2025, 1, 1);
        LocalDate lastDayOfYear = LocalDate.of(2025, 12, 31);
        LocalDate firstTuesdayInMonth = LocalDate.of(2025, 7, 1); // 1 июля 2025 - вторник
        LocalDate lastMondayInMonth = LocalDate.of(2025, 7, 28); // 28 июля 2025 - последний понедельник
        LocalDate dateInQ1 = LocalDate.of(2025, 2, 15);
        LocalDate dateInQ4 = LocalDate.of(2025, 11, 10);
        LocalDate dateFirstHalf = LocalDate.of(2025, 7, 10);
        LocalDate dateSecondHalf = LocalDate.of(2025, 7, 20);
        LocalDate dateWith31Days = LocalDate.of(2025, 1, 15);
        LocalDate dateWith30Days = LocalDate.of(2025, 4, 15);
        LocalDate isoEraDate = LocalDate.of(2025, 7, 3);
        LocalDate formattedDate = LocalDate.of(2025, 10, 26);

        EntityValidator.of(entity).shouldHave(
                // Базовые сравнения
                value(MyEntity::eventDate, dateBefore(tomorrow)),
                value(MyEntity::eventDate, dateAfter(yesterday)),
                value(MyEntity::eventDate, dateIsInFuture()), // Предполагается, что eventDate в будущем
                value(MyEntity::eventDate, dateIsInPast()),   // Предполагается, что eventDate в прошлом
                value(MyEntity::eventDate, dateEquals(today)),
                value(MyEntity::eventDate, dateIsToday()),
                value(MyEntity::eventDate, dateIsYesterday()),
                value(MyEntity::eventDate, dateIsTomorrow()),
                value(MyEntity::eventDate, dateAfterOrEqual(today.minusDays(5))),
                value(MyEntity::eventDate, dateBeforeOrEqual(today.plusDays(5))),
                value(MyEntity::eventDate, dateIsBetween(today.minusDays(1), today.plusDays(1))),
                // Проверки по частям даты
                value(MyEntity::eventDate, dateHasYear(2025)),
                value(MyEntity::eventDate, dateHasMonth(7)),
                value(MyEntity::eventDate, dateHasMonth(Month.JULY)),
                value(MyEntity::eventDate, dateHasDayOfMonth(3)),
                value(MyEntity::eventDate, dateHasDayOfYear(184)), // Для 3 июля 2025 года (невисокосный)
                value(MyEntity::eventDate, dateHasDayOfWeek(DayOfWeek.THURSDAY)),
                // Календарные свойства
                value(MyEntity::eventDate, dateIsLeapYear()), // Для 2024, 2028 и т.д.
                value(MyEntity::eventDate, dateIsNotLeapYear()), // Для 2023, 2025 и т.д.
                value(MyEntity::eventDate, dateIsWeekend()), // Для субботы или воскресенья
                value(MyEntity::eventDate, dateIsWeekday()), // Для понедельника-пятницы
                // Сравнения по периодам и частям
                value(MyEntity::eventDate, dateWithin(today.plusDays(2), Period.ofDays(3))), // Разница 2 дня, допуск 3 дня
                value(MyEntity::eventDate, dateDiffersByAtLeast(today.minusDays(5), Period.ofDays(4))), // Разница 5 дней, минимум 4 дня
                value(MyEntity::eventDate, dateIsInSameMonthAs(today.minusDays(10))), // Месяц тот же, даже если дни разные
                value(MyEntity::eventDate, dateIsInSameYearAs(today.plusMonths(3))), // Год тот же, даже если месяц и день разные
                value(MyEntity::eventDate, dateIsInSameWeekAs(today.plusDays(2))), // Если в пределах одной ISO недели
                value(MyEntity::eventDate, dateIsInSameWeekAs(today.plusDays(2), Locale.US)), // С учетом локали
                // Проверки по границам периодов
                value(MyEntity::eventDate, dateIsFirstDayOfMonth()),
                value(MyEntity::eventDate, dateIsLastDayOfMonth()),
                value(MyEntity::eventDate, dateIsFirstDayOfYear()),
                value(MyEntity::eventDate, dateIsLastDayOfYear()),
                value(MyEntity::eventDate, dateIsFirstInMonth(DayOfWeek.TUESDAY)), // Проверить, является ли eventDate первым вторником
                value(MyEntity::eventDate, dateIsLastInMonth(DayOfWeek.MONDAY)), // Проверить, является ли eventDate последним понедельником
                // Кварталы и половины месяца
                value(MyEntity::eventDate, dateIsInQuarter(3)), // Текущая дата (июль) в 3-м квартале
                value(MyEntity::eventDate, dateIsInFirstHalfOfMonth()),
                value(MyEntity::eventDate, dateIsInSecondHalfOfMonth()),
                // Дополнительные продвинутые проверки
                value(MyEntity::eventDate, dateHasDaysInMonth(31)), // Для июля 31 день
                value(MyEntity::eventDate, dateMatchesPattern("dd.MM.yyyy", "03.07.2025")) // Соответствие формату
        );
    }

    /**
     * Валидация LocalDateTimeAssertions.
     */
    public void validateLocalDateTimeAssertions(MyEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime specificLocalDateTime = LocalDateTime.of(2025, 6, 8, 12, 30, 0, 0); // 8 июня 2025, 12:30:00

        EntityValidator.of(entity).shouldHave(
                // Общие проверки на до/после/равенство/диапазон
                value(MyEntity::localDateTimeCreated, dateTimeBefore(now.plusHours(1))), // Дата и время должны быть до сейчас + 1 час
                value(MyEntity::localDateTimeUpdated, dateTimeAfter(now.minusDays(1))), // Дата и время должны быть после сейчас - 1 день
                value(MyEntity::localDateTimeEventStart, dateTimeIsInFuture()), // Дата и время должны быть в будущем
                value(MyEntity::localDateTimeCreated, dateTimeIsInPast()), // Дата и время должны быть в прошлом
                value(MyEntity::localDateTimeCreated, dateTimeEquals(specificLocalDateTime)), // Дата и время должны быть равны конкретной дате и времени
                value(MyEntity::localDateTimeUpdated, dateTimeAfterOrEqual(now.minusMinutes(5))), // Дата и время должны быть после или равны сейчас - 5 минут
                value(MyEntity::localDateTimeCreated, dateTimeBeforeOrEqual(now)), // Дата и время должны быть до или равны сейчас
                value(MyEntity::localDateTimeEventStart, dateTimeIsBetween(now, now.plusDays(2))), // Дата и время должны быть в диапазоне между сейчас и сейчас + 2 дня
                // Проверки с допуском
                value(MyEntity::localDateTimeUpdated, dateTimeWithin(now, Duration.ofSeconds(10))), // Дата и время должны быть в пределах 10 секунд от сейчас
                // Проверки по компонентам даты/времени
                value(MyEntity::localDateTimeCreated, dateTimeHasYear(2025)), // Год должен быть 2025
                value(MyEntity::localDateTimeCreated, dateTimeHasMonth(6)), // Месяц должен быть 6 (июнь)
                value(MyEntity::localDateTimeCreated, dateTimeHasMonth(Month.JUNE)), // Месяц должен быть июнь
                value(MyEntity::localDateTimeCreated, dateTimeHasDayOfMonth(8)), // День месяца должен быть 8
                value(MyEntity::localDateTimeCreated, dateTimeHasDayOfYear(159)), // День года должен быть 159 (для 8 июня 2025)
                value(MyEntity::localDateTimeCreated, dateTimeHasHour(12)), // Час должен быть 12
                value(MyEntity::localDateTimeCreated, dateTimeHasMinute(30)), // Минута должна быть 30
                value(MyEntity::localDateTimeCreated, dateTimeHasSecond(0)), // Секунда должна быть 0
                // Проверки совпадения по компонентам
                value(MyEntity::localDateTimeCreated, dateTimeIsOnSameDayAs(specificLocalDateTime)), // Дата должна совпадать с опорной датой
                value(MyEntity::localDateTimeCreated, dateTimeIsWeekend()), // Дата должна приходиться на выходной (воскресенье)
                value(MyEntity::localDateTimeUpdated, dateTimeIsWeekday()), // Дата должна приходиться на рабочий день
                value(MyEntity::localDateTimeCreated, dateTimeIsEqualToIgnoringNanos(LocalDateTime.of(2025, 6, 8, 12, 30, 0, 123456))), // Дата и время должны быть равны, игнорируя наносекунды
                value(MyEntity::localDateTimeCreated, dateTimeHasDayOfWeek(DayOfWeek.SUNDAY)), // День недели должен быть воскресенье
                value(MyEntity::localDateTimeCreated, dateTimeIsInSameMonthAs(LocalDateTime.of(2025, 6, 1, 0, 0))), // Дата должна быть в том же месяце, что и опорная
                value(MyEntity::localDateTimeCreated, dateTimeIsInSameYearAs(LocalDateTime.of(2025, 1, 1, 0, 0))), // Дата должна быть в том же году, что и опорная
                value(MyEntity::localDateTimeCreated, dateTimeIsInSameWeekAs(LocalDateTime.of(2025, 6, 9, 0, 0))), // Дата должна быть в той же ISO-неделе, что и опорная (8 и 9 июня 2025 в одной неделе)
                // Проверки на минимальную/максимальную разницу
                value(MyEntity::localDateTimeUpdated, dateTimeDiffersByAtLeast(now.minusHours(1), Duration.ofMinutes(10))), // Разница между датами должна быть не меньше 10 минут
                value(MyEntity::localDateTimeEventStart, dateTimeIsFutureBy(Duration.ofDays(1))), // Дата и время должны быть как минимум на 1 день в будущем от сейчас
                value(MyEntity::localDateTimeCreated, dateTimeIsPastBy(Duration.ofDays(1))), // Дата и время должны быть как минимум на 1 день в прошлом от сейчас
                // Проверки на начало/конец единиц времени
                value(MyEntity::localDateTimeCreated, dateTimeIsAtStartOfDay()), // Время должно быть 00:00:00
                value(MyEntity::localDateTimeEventEnd, dateTimeIsAtEndOfDay()), // Время должно быть 23:59:59.999999999
                // Проверки по кварталам и частям дня
                value(MyEntity::localDateTimeCreated, dateTimeIsInQuarter(2)), // Дата должна быть во втором квартале
                value(MyEntity::localDateTimeCreated, dateTimeHasSameHourAndMinute(LocalDateTime.of(2025, 6, 8, 12, 30))), // Час и минута должны совпадать с опорной датой
                value(MyEntity::localDateTimeCreated, dateTimeIsBetweenTimeOfDay(LocalTime.of(12, 0), LocalTime.of(13, 0))), // Время должно быть между 12:00 и 13:00
                value(MyEntity::localDateTimeCreated, dateTimeHasNanoOfSecond(0)), // Наносекунды должны быть 0
                // Проверки с игнорированием секунд
                value(MyEntity::localDateTimeCreated, dateTimeIsAfterIgnoringSeconds(LocalDateTime.of(2025, 6, 8, 12, 29, 59))), // Время (без секунд) должно быть после 12:29
                value(MyEntity::localDateTimeCreated, dateTimeIsBeforeIgnoringSeconds(LocalDateTime.of(2025, 6, 8, 12, 30, 1))), // Время (без секунд) должно быть до 12:30
                // Проверки на свойства года/месяца
                value(MyEntity::localDateTimeCreated, dateTimeIsLeapYear()), // Год должен быть високосным
                value(MyEntity::localDateTimeCreated, dateTimeIsFirstDayOfMonth()), // Дата должна быть первым днем месяца
                value(MyEntity::localDateTimeCreated, dateTimeIsLastDayOfMonth()), // Дата должна быть последним днем месяца
                value(MyEntity::localDateTimeCreated, dateTimeIsInFirstHalfOfMonth()), // Дата должна быть в первой половине месяца (1-15)
                value(MyEntity::localDateTimeCreated, dateTimeIsInSecondHalfOfMonth()), // Дата должна быть во второй половине месяца (16-конец)
                // Проверки на части суток
                value(MyEntity::localDateTimeCreated, dateTimeIsMorning()), // Время должно быть утром (06:00 - 12:00)
                value(MyEntity::localDateTimeCreated, dateTimeIsAfternoon()), // Время должно быть днем (12:00 - 18:00)
                value(MyEntity::localDateTimeCreated, dateTimeIsEvening()), // Время должно быть вечером (18:00 - 22:00)
                value(MyEntity::localDateTimeCreated, dateTimeIsNight()), // Время должно быть ночью (22:00 - 06:00)
                // Проверка количества дней в месяце
                value(MyEntity::localDateTimeCreated, dateTimeHasDaysInMonth(30)), // Месяц должен иметь 30 дней (июнь)
                // Упрощенные проверки на "в пределах N единиц"
                value(MyEntity::localDateTimeUpdated, dateTimeIsWithinMinutesOf(now, 1)), // Разница с опорной датой не более 1 минуты
                value(MyEntity::localDateTimeUpdated, dateTimeIsWithinHoursOf(now, 1)), // Разница с опорной датой не более 1 часа
                value(MyEntity::localDateTimeUpdated, dateTimeIsWithinSecondsOf(now, 60)) // Разница с опорной датой не более 60 секунд
        );
    }

    /**
     * Валидация PropertyAssertions.
     */
    public void validatePropertyAssertions(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                value(MyEntity::name, propertyEqualsTo("John Doe")), // значение имени равно "John Doe"
                value(MyEntity::middleName, propertyIsNull()), // отчество равно null
                value(MyEntity::name, propertyIsNotNull()), // имя не равно null
                value(MyEntity::tags, propertyIsEmpty()), // список тегов пуст (для коллекции)
                value(MyEntity::blankString, propertyIsEmpty()), // пустая строка является пустой
                value(MyEntity::attributes, propertyIsNotEmpty()), // карта атрибутов не пуста
                value(MyEntity::age, propertyIsOfType(Integer.class)), // возраст имеет тип Integer
                value(MyEntity::name, propertyIsNotOfType(Integer.class)), // имя не имеет тип Integer
                value(MyEntity::roles, propertyIsInstanceOf(List.class)), // роли являются экземпляром List
                value(MyEntity::age, propertyIsNotInstanceOf(List.class)), // возраст не является экземпляром List
                value(MyEntity::roles, propertyIsAssignableFrom(List.class)), // класс ролей реализует интерфейс List
                value(MyEntity::address, propertyAllPropertiesEqual(Map.of(Address::city, "Moscow", Address::street, "Tverskaya"))), // город и улица адреса соответствуют ожидаемым
                value(MyEntity::status, propertyIn(Arrays.asList("ACTIVE", "INACTIVE"))), // статус входит в список ["ACTIVE", "INACTIVE"]
                value(MyEntity::status, propertyNotIn(Arrays.asList("DELETED", "ARCHIVED"))), // статус не входит в список ["DELETED", "ARCHIVED"]
                value(MyEntity::age, propertyGreaterThan(18)), // возраст больше 18
                value(MyEntity::age, propertyGreaterThanOrEqualTo(21)), // возраст больше или равен 21
                value(MyEntity::age, propertyLessThan(65)), // возраст меньше 65
                value(MyEntity::age, propertyLessThanOrEqualTo(60)), // возраст меньше или равен 60
                value(MyEntity::age, propertyBetween(18, 65)), // возраст находится в диапазоне [18, 65]
                value(MyEntity::age, propertyToStringEquals("25")), // строковое представление возраста равно "25"
                value(MyEntity::id, propertyToStringStartsWith("ID-")), // строковое представление id начинается с "ID-"
                value(MyEntity::id, propertyToStringEndsWith("-001")), // строковое представление id заканчивается на "-001"
                value(MyEntity::id, propertyToStringMatchesRegex("ID-\\d{3}")), // строковое представление id соответствует рег. выражению "ID-\\d{3}"
                value(MyEntity::description, propertyContainsSubstring("important")), // описание содержит подстроку "important"
                value(MyEntity::description, propertyContainsSubstringIgnoringCase("IMPORTANT")), // описание содержит подстроку "IMPORTANT" без учета регистра
                value(MyEntity::name, propertySatisfies(name -> Assertions.assertThat(name.length()).isPositive())), // имя удовлетворяет заданному условию (длина положительная)
                value(MyEntity::score, propertyMatchesPredicate(s -> s.compareTo(BigDecimal.ZERO) > 0, "score is positive")), // score удовлетворяет предикату (положительное число)
                value(MyEntity::attributes, propertyMapContainsKey("color")), // карта атрибутов содержит ключ "color"
                value(MyEntity::attributes, propertyMapDoesNotContainKey("weight")), // карта атрибутов не содержит ключ "weight"
                value(MyEntity::attributes, propertyMapContainsEntry("color", "blue")), // карта атрибутов содержит запись "color":"blue"
                value(MyEntity::attributes, propertyMapContainsValue("blue")), // карта атрибутов содержит значение "blue"
                value(MyEntity::attributes, propertyMapDoesNotContainValue("red")), // карта атрибутов не содержит значение "red"
                value(MyEntity::emptyMap, propertyMapIsEmpty()), // карта пуста
                value(MyEntity::attributes, propertyMapIsNotEmpty()), // карта атрибутов не пуста
                value(MyEntity::name, propertyHasLength(8)), // длина имени равна 8
                value(MyEntity::sortedList, propertyIsSorted()), // список отсортирован
                value(MyEntity::roles, propertyHasSize(3)), // размер списка ролей равен 3
                value(MyEntity::roles, propertyHasSizeGreaterThan(1)), // размер списка ролей больше 1
                value(MyEntity::roles, propertyHasSizeLessThan(5)), // размер списка ролей меньше 5
                value(MyEntity::blankString, propertyIsBlank()), // строка пустая или состоит из пробелов
                value(MyEntity::name, propertyIsNotBlank()), // строка не пустая и не состоит из пробелов
                value(MyEntity::roleEntities, propertyEveryElementSatisfies(roles -> roles, role -> Assertions.assertThat(role.name()).isNotBlank())), // каждая роль в списке имеет непустое имя
                value(MyEntity::scores, propertyAllElementsMatchPredicate(s -> s > 0, "all scores are positive")), // все элементы в списке очков удовлетворяют предикату (положительные)
                value(MyEntity::scores, propertyAnyElementMatchesPredicate(s -> s == 100, "at least one score is 100")), // хотя бы один элемент в списке очков равен 100
                value(MyEntity::roles, propertyCollectionContains("EDITOR")), // коллекция ролей содержит "EDITOR"
                value(MyEntity::status, propertyStringMatchesRegexIgnoringCase("^(active|inactive)$")), // статус соответствует рег. выражению без учета регистра
                value(MyEntity::status, propertyEqualsIgnoreCase("active")), // статус равен "active" без учета регистра
                value(MyEntity::uniqueRoles, propertyCollectionHasUniqueElements()), // коллекция ролей содержит только уникальные элементы
                value(MyEntity::name, propertyMatches(org.hamcrest.Matchers.containsString("Doe"))), // имя соответствует Hamcrest матчеру (содержит "Doe")
                value(MyEntity::name, propertyDoesNotMatch(org.hamcrest.Matchers.equalTo("EDITOR"))) // имя не соответствует Hamcrest матчеру (не равно "EDITOR")
        );
    }

    /**
     * Валидация ListAssertions.
     */
    public void validateListAssertions(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                // Проверки размера и наличия
                value(MyEntity::tags, listIsEmpty()), // список тегов должен быть пуст
                value(MyEntity::roles, listIsNotEmpty()), // список ролей не должен быть пуст
                value(MyEntity::roles, listCountEqual(3)), // количество ролей в списке равно 3
                value(MyEntity::roles, listCountLessThan(5)), // количество ролей в списке меньше 5
                value(MyEntity::roles, listCountGreaterThan(2)), // количество ролей в списке больше 2
                value(MyEntity::roles, listHasSizeBetween(2, 4)), // количество ролей в списке от 2 до 4 (включительно)
                // Проверки содержимого списка
                value(MyEntity::roles, listContainsElement("EDITOR")), // список ролей содержит "EDITOR"
                value(MyEntity::roles, listContainsAllElements("EDITOR", "USER")), // список ролей содержит "EDITOR" и "USER"
                value(MyEntity::roles, listContainsOnly("EDITOR", "USER", "MODERATOR")), // список ролей содержит только указанные элементы
                value(MyEntity::roles, listMatchesExactly(List.of("EDITOR", "MODERATOR", "USER"))), // список ролей в точности соответствует указанному, включая порядок
                value(MyEntity::roles, listMatchesExactlyUnordered(List.of("USER", "EDITOR", "MODERATOR"))), // список ролей содержит те же элементы, без учета порядка
                value(MyEntity::roleEntities, listNoNulls()), // список сущностей ролей не содержит null-элементов
                // Проверки уникальности и дубликатов
                value(MyEntity::uniqueRoles, listEntitiesAreUnique()), // все элементы в списке уникальны
                value(MyEntity::roles, listHasDuplicates()), // в списке строковых ролей есть дубликаты
                value(MyEntity::roleEntities, listDistinctBy(Role::id)), // все роли в списке имеют уникальный ID
                value(MyEntity::roleEntities, listEntitiesPropertyAreDistinct(Role::name)), // у всех ролей в списке уникальные имена
                // Условные проверки элементов
                value(MyEntity::roleEntities, listAllMatch(value(Role::description, hasNonBlankContent()))), // все роли в списке имеют непустое описание
                value(MyEntity::roleEntities, listAnyMatch(value(Role::name, equalTo("EDITOR")))), // хотя бы одна роль в списке имеет имя "EDITOR"
                value(MyEntity::roleEntities, listNoneMatch(value(Role::name, equalTo("GUEST")))), // ни одна роль в списке не имеет имени "GUEST"
                value(MyEntity::roleEntities, listExactlyMatches(value(Role::name, equalTo("SYSTEM")), 1)), // ровно одна роль имеет тип "SYSTEM"
                value(MyEntity::roleEntities, listAtLeastMatches(value(Role::enabled, isTrue()), 2)), // как минимум две роли активны
                value(MyEntity::roleEntities, listAtMostMatches(value(Role::legacy, isTrue()), 1)), // не более одной роли является устаревшей
                // Проверки сортировки
                value(MyEntity::roleEntities, listIsSorted(Comparator.comparing(Role::creationDate))), // список ролей отсортирован по дате создания (по возрастания)
                value(MyEntity::roleEntities, listIsSortedDescending(Comparator.comparing(Role::creationDate))), // список ролей отсортирован по дате создания (по убыванию)
                // Агрегирующие и групповые проверки
                value(MyEntity::roleEntities, listSumEqual(Role::permissionCount, 15.0)), // сумма количества разрешений для всех ролей равна 15
                value(MyEntity::roleEntities, listAverageEqual(Role::permissionCount, 5.0)), // среднее количество разрешений для роли равно 5
                value(MyEntity::roleEntities, listValuesEqual(Role::tenantId, "TENANT-123")), // у всех ролей одинаковый tenantId
                value(MyEntity::roleEntities, listGroupedBySize(Role::type, Map.of("EDITOR", 1, "USER", 2))) // размеры групп ролей по типу: 1 админ, 2 пользователя
        );
    }

    /**
     * Валидация CollectionAssertions
     */
    public void validateCollectionAssertions(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                value(MyEntity::roles, collectionLengthEquals(3)), // длина коллекции roles равна 3
                value(MyEntity::roles, collectionLengthGreaterThan(1)), // длина коллекции roles больше 1
                value(MyEntity::roles, collectionLengthLessThan(5)), // длина коллекции roles меньше 5
                value(MyEntity::tags, collectionEmpty()), // коллекция tags пуста
                value(MyEntity::roles, collectionContains("EDITOR")), // коллекция roles содержит элемент "EDITOR"
                value(MyEntity::roles, collectionContainsAll("EDITOR", "USER")), // коллекция roles содержит все элементы "EDITOR" и "USER"
                value(MyEntity::roles, collectionNoDuplicates()), // коллекция roles не содержит дубликатов
                value(MyEntity::roles, collectionLengthBetween(2, 4)), // длина коллекции roles находится в диапазоне от 2 до 4
                value(MyEntity::name, collectionStartsWith("Te")), // строка name начинается с "Te" (применимо к строкам как к коллекциям символов)
                value(MyEntity::roles, collectionStartsWith("EDITOR")), // коллекция roles начинается с элемента "EDITOR"
                value(MyEntity::name, collectionEndsWith("st")), // строка name заканчивается на "st" (применимо к строкам как к коллекциям символов)
                value(MyEntity::roles, collectionEndsWith("USER")), // коллекция roles заканчивается элементом "USER"
                value(MyEntity::roles, collectionAllElementsInstanceOf(String.class)), // все элементы коллекции roles являются строками
                value(MyEntity::roles, collectionLengthGreaterThanOrEqual(2)), // длина коллекции roles больше или равна 2
                value(MyEntity::roles, collectionLengthLessThanOrEqual(4)), // длина коллекции roles меньше или равна 4
                value(MyEntity::roles, collectionHasSameSizeAs(Arrays.asList("A", "B", "C"))), // коллекция roles имеет тот же размер, что и список ["A", "B", "C"]
                value(MyEntity::roles, collectionContainsAtLeast(1, "EDITOR")), // коллекция roles содержит "EDITOR" как минимум 1 раз
                value(MyEntity::roles, collectionOccurrenceCountEquals("EDITOR", 1)) // элемент "EDITOR" встречается ровно 1 раз в коллекции roles
        );
    }
}
