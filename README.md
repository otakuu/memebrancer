# memebrancer
JavaPlay App that provides an UI for managing Birthdays or other events. A cronjob sends all the current events to an email-adress.

## Setup
Create a file conf/application.conf

```
## App config
storageFilePath="/var/share/birthdays.txt"
gmailUsername="test@gmail.com"
gmailPassword="changeMe"
emailTo="mailToSent@gmail.com"

## Startup Modul
play.modules.enabled += "utils.OnStartupModule"

## Filters
play.filters {
  csrf {
    cookie.secure=true
  }
  headers {
    frameOptions="DENY"
    xssProtection="1; mode=block"
    contentTypeOptions="nosniff"
    permittedCrossDomainPolicies="master-only"
    contentSecurityPolicy="default-src 'self' ; frame-src 'self'  https://www.google.com/recaptcha/ ; script-src 'self' https://apis.google.com https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/ 'unsafe-inline'; style-src 'self' 'unsafe-inline' https://apis.google.com https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/; object-src 'none';"
  }
  hosts {
    allowed = ["."]  # 127.0.0.1:9000
  }     
}
```

And a birthdays.txt file **don't forget the newline at the end!**:
```
20110103;1;Mama
19800104;0;GranMa
```
1=Birthday, 0=Dead

## Running the app
```
sbt run
```
localhost:9000
