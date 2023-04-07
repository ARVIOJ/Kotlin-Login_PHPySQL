package app

class AppConfig {

    fun getURL(string: String): String? {
        var url : String? = ""
        if(string == "login"){
            url = "https://arviojandroid.000webhostapp.com/Login.php"}
        else if(string == "register"){
            url = "https://arviojandroid.000webhostapp.com/Register.php"}
        return url
    }

}