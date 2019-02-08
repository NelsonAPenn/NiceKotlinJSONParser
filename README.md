# Sleek JSONParser

I created this Nice Kotlin JSON Parser due to the immense pain that GSON causes me. It is still in the early stages of development, but it does the job. 

## Installation

Just download the Kotlin file and add it into your project.

## Usage

- You first make a `JSONData` object on the JSON string you want to extract values from.

- The index operator `[]` finds the ith element of a JSONData object (that is based on a JSON array), and returns another JSONData object containing said nth element.

- The mod operator `%`, placed between a JSONData object (that is based on a JSON object) and a string parameter name, returns a JSONData object based on the value of that property.

- When you have a JSONData object that is based on a single value, simply use `.value` to get it out into a string.

- Chain these for a nice JSON parsing experience!

## Example

  ```kotlin
  is Result.Success->{
    val data=result.get()
    
    //usage of my Nice Kotlin JSON parser
    
    var jsonData=JSONData(data)
    toast((jsonData[1]%"login_token").value)
    toast(jsonData[2].value)
    
    //end usage of my Nice Kotlin JSON Parser
    
    done=true//success, get token, decide where to send user
  }
  ```

The above block of code is a sample from Fuel post request. I receive a JSON array from my request, and create a `JSONData` object using the constructor on the `data` string I received. I then toast the login token (by accessing the login_token property of the second (index of 1) item in the JSON array with `jsonData[1]` (which I know is an object), then access the value of the property with `%"login_token"`, and finally accessing the `value` property to get the single (non-object, non-array) value out. The third element in my JSON array is just a string, so I access it using the indexing operator, then immediately use `.value` to get it out and toast it.

## Disclaimer

- This parser does not check to make sure that the JSON string is valid JSON nor does it check if the operation you are trying to run is stupid.

- You must know what the structure of the JSON you are trying to inspect is, this class is merely designed to traverse it, and get values from it.

- There is an overwhelmingly large chance that the implementation has bugs, unhandled edge cases. Please post them in the bug reports section.
