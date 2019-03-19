# Sleek JSONParser

I created this nice Kotlin JSON Parser to be a simple, lightweight, and painless way to navigate and extract values from JSON data as well as build JSON objects.

## Installation

Add this project as a submodule to your Android application with Import Gradle Module

OR

Just download the Kotlin file Sleek.kt and add it into your project.

## Usage

- You first make a `Sleek` object on the JSON string you want to extract values from.

- The index operator `[]` finds the ith element of a Sleek object (that is based on a JSON array), and returns another Sleek object containing said nth element.

- The mod operator `%`, placed between a Sleek object (that is based on a JSON object) and a string parameter name, returns a Sleek object based on the value of that property.

- Chain these for a nice JSON parsing experience!

- When you have a Sleek object that is based on a single value, simply use `.string` to get it out into a string, or `.int` to get it out as an int.

- When you have a Sleek object that is based on a JSON array, you have the option of using `.array` to return an array of type `Array<Sleek>` containing the entries of the JSON array.

- When you have a Sleek object that is based on a JSON object, you have the option of using `.map` to return a map of type `Map<String,Sleek>` containing the fields of the object.

- Chain these for a nice JSON parsing experience!

- `toString()` returns the JSON source string for the object 

- You can also use `isJSONObject`, `isJSONArray`, and `isJSONLiteral` with obvious effects.

## Example

  ```kotlin
    var jsonData=Sleek("[1,\"thing\",{\"thang\":24}]")
    
    (jsonData[2]%"thang").int
    //24
    
    jsonData[1].string
    //thing
    
    jsonData.array
    //An array containing a Sleek object of each element of the JSON array: 1, "thing", and {"thang":24}
    
    jsonData[2].map
    //A map containing the pair "thang" and a Sleek object based on 24.
    
    
    //end usage of my Sleek JSON Parser
  ```
