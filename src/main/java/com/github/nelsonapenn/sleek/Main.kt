package com.github.nelsonapenn.sleek

fun main()
{
    var x = Sleek("""{"widget": {
    "debug": "on",
    "window": {
        "title": "Sample Konfabulator Widget",
        "name": "main_window",
        "width": 500,
        "height": 500
    },
    "emptyMap": {},
    "one map": {
        "thing": "thang"
    },
    "empty Array": [],
    "one array": [
        3
    ],
    "two array": [
        3, null
    ],
    "mapTest": {
        "ONE": 3.5,
        "two": 39,
        "three": 40,
        "true": 34,
        "seven": 33
    },
    "image": { 
        "src": "Images/Sun.png",
        "name": ["a \nstring", "another string", "a third thingy", "a fourth thingy", 5.0, true],
        "hOffset": 250,
        "vOffset": 250,
        "alignment": "center"
    },
    "text": {
        "data": "Click Here",
        "size": 36,
        "style": "bold",
        "name": "te\"x\t\u6b691",
        "hOffset": -250.12e4,
        "booleanTest": true,
        "vOffset": 100,
        "alignment": "center",
        "onMouseUp": "sun1.opacity = (sun1.opacity / 100) * 90;"
    }
}}""")
    var name = x["widget"]["text"]["name"];
    print(name.string)
    var other = x["widget"]["mapTest"].map
    var other2 = x["widget"]["mapTest"].floatMap
    var other3 = x["widget"]["one array"].intList
    var other4 = x["widget"]["empty Array"].stringList
    var other5= x["widget"]["two array"].intList
    var other6= x["widget"]["one array"].stringList
    var y = 0
}