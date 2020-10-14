# Sleek JSON Parser

I created this JSON Parser to be a simple, lightweight, and painless way to navigate and extract values from JSON data.

Benefits:

- Flexible: no need to create data classes into which JSON data is deserialized.
- Lightweight: mere kilobytes, no other third-party dependencies.

## Installation

Add this project as a submodule to your Android application with Import Gradle Module

OR

Just download the Kotlin file Sleek.kt and add it into your project.

## Usage

First instantiate a `Sleek` object from the JSON data. The constructor accepts the source JSON string. Afterwards:

- If the root JSON value is an object, the index operator `[a]` (where `a` is of type `String`) returns a `Sleek` object containing the value in the dictionary associated with key `a`. If not found, throws `JsonKeyException`

- If the root JSON value is an array, the index operator `[a]` (where `a` is of type `Int`) returns a `Sleek` object containing the value in the array at index `a`. If not found, throws   `JsonArrayIndexOutOfBoundsException`.

- When you have a Sleek object that contains a single JSON literal, simply use:
  - `.string` to return a `String?`
  - `.int` to return a `Int?`
  - `.float` to return a `Float?`
  - `.boolean` to return a `Boolean?`
  - All of the above values are `null` only if the value is a JSON `null` literal. Attempting to cast to a mismatched type throws an exception.

- When you have a Sleek object that is based on a JSON array, you have the option of using `.list` to return an array of type `List<Sleek>` containing the entries of the JSON array.

- Strictly typed lists are also available. Use
  - `stringList` to return `List<String?>`
  - `intList` to return `List<Int?>`
  - `floatList` to return `List<Float?>`
  - `booleanList` to return `List<Boolean?>`

- When you have a Sleek object that is based on a JSON object, you have the option of using `.map` to return a map of type `Map<String, Sleek>` containing the fields of the object.

- Strictly typed maps are also available. Use
  - `stringMap` to return `Map<String, String?>`
  - `intMap` to return `Map<String, Int?>`
  - `floatMap` to return `Map<String, Float?>`
  - `booleanMap` to return `Map<String, Boolean?>`

- Chain these for a nice JSON parsing experience!

- You can also use:
  - `isJsonObject`
  - `isJsonArray`
  - `isJsonLiteral`
  - `isJsonNumber`
  - `isJsonString`
  - `isJsonBoolean`
  - `isJsonNullValue`
  - with obvious effects.

## Example

  ```kotlin
    val jsonData = Sleek("""[ 1, "thing", { "thang": 24 } ]""")
    val jsonData2 = Sleek("""[1, 2, 3.5, 4, null, -7.1e4]""")

    jsonData[2]["thang"].int
    //24

    jsonData[1].string
    //thing

    jsonData.list
    //An array containing a Sleek object of each element of the JSON array: 1, "thing", and {"thang":24}

    jsonData[2].map
    //A map containing the pair "thang" and a Sleek object based on 24.

    jsonData2.floatList
    // List<Int?> => [1.0, 2.0, 3.5, 4.0, null, -71000.0]

  ```
