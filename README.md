# Sensitive data masking

Applications that incorporate sensitive data such as Social Security and credit card numbers may inadvertently disclose
that data into locations such as logs and traces, or through careless UI usage. Often, the disclosure occurs through an
implicit `toString()` invocation. One way to mitigate this risk is to ensure that the objects holding the  sensitive
data do not reveal it by default.

# Examples

## Sensitive Object

Suppose you have a sensitive object of type `T`. To protect from inadvertent disclosure, you wrap it in an object of 
type `Sensitive<T>`. Now you can pass `myWrapper` without having to worry about a stray `myWrapper.toString()` causing
the sensitive data to appear in a log file.
```Java
PersonalData mySensitiveData = new PersonalData (12345);
Sensitive<PersonalData> myWrapper = new Sensitive<PersonalData> (mySensitiveData);
System.out.println(myWrapper);
```
produces a blank line:
```Text
```

## Sensitive String

One common case is the sensitive data is a `String`. The `MaskedField` type extends `Sensitive<CharSequence>` with some
 additional default behavior using `#` as a masking character to replace characters in the sensitive string:
```Java
MaskedField mySensitiveField = new MaskedField("Shhh");
System.out.println("Explicit toString(): " + mySensitiveField.toString());
System.out.println("Implicit toString(): " + mySensitiveField);
System.out.printf("Default format: %s", mySensitiveField);
```
The code above produces:
```Text
Explicit toString(): ####
Implicit toString(): ####
Default format: ####
```
Using this type, data can be exposed explicitly using the precision specifier in string formatting, as follows:
```Java
System.out.printf("Partially exposed: %.2s", mySensitiveField);
System.out.printf("More exposed: %.3s", mySensitiveField);
System.out.printf("Masked with formatting: `%6S`", mySensitiveField);
System.out.printf("More formatting: `%#-6.1S`", mySensitiveField);
```
The code above produces:
```Text
Partially exposed: ##hh
More exposed: #hhh
Masked formatting: `  ##HH`
More formatting: `###H  `
```
Note from the examples above that the "alternate" form has no effect on the output.

# Implementation

The `Sensitive` class holds the data in a final, protected, transient property with no predefined accessor methods.
Accessors can be added to subclasses if desired. The property is transient to ensure it is not exposed via object
serialization.

Responsibility for rendering the sensitive data is delegated to a redaction function returned by the `redactor()`
method. The redactor function accepts two parameters: the sensitive object to redact, and the desired precision of the
output. The precision is generally interpreted as the number of non-redacted characters to include in the output. The
default redactor always returns an empty string. 

The default implementation of `alternate()` simply delegates to the `redactor()` method. Override the `alternate()`
method if the sensitive data has an alternate rendition.

The `Sensitive` object implements the `Formattable` interface, and the `formatTo(……)` is responsible for applying 
formatting to the rendered, protected data as needed.

The `hashCode()` method delegates to the hash code of the protected object.

The `equals()` method provides the usual short-circuit checks for the argument being the same object and the argument
being the same class, then delegates to the equals method of the protected object.

The `toString()` method delegates to the string formatter, using `String.format("%s", this)`.

## MaskedField

`MaskedField` extends `Sensitive<CharSequence>` for the common case of protected string and string-like values. The
redactor supplied by the `MaskedField` subclass replaces protected  characters with `#` up to the number of non-redacted
characters specified by the precision. Redaction starts on the left, so the rightmost characters are exposed. A masking
character other than `#` can be passed to the constructor.

## SensitiveArray

`SensitiveArray` extends `Sensitive` for cases where the protected data is an array. It overrides the `hashCode()` and
`equals()` methods to use the corresponding functions provided by `java.util.Arrays`.


