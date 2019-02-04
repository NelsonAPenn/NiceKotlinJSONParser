# NiceKotlinJSONParser

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
                                var jsonData=JSONData(data)
                                toast((jsonData[1]%"login_token").value)
                                toast((jsonData[2]).value)
                                done=true//success, get token, decide where to send user based on organization/individuals thing in data.
                            }

## Disclaimer

- This parser does not check to make sure that the JSON string is valid JSON nor does it check if the operation you are trying to run is stupid. Don't be stupid.

- There is an overwhelmingly large chance that the implementation has bugs, unhandled edge cases. Please post them in the bug reports section.
