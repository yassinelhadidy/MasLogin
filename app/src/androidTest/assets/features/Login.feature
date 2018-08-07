Feature: As a merchant, I want to be able to log-in to the application, to be able to start payment process
  and manage your wallet settings.

Scenario: Merchant login in with successful credentials
 Given Merchant at the login page
 When He inserts his credentials successfully
 Then He will be logged to the app successfully

Scenario Outline: Unsuccessfull merchant login to the App
 Given Merchant at the login page
 When Merchant login in using <invalid credentials>
 Then This <error message> is displayed

 Examples:
   |invalid credentials      |error message                                            |
   |wrong credentials        |برجاء التأكد من كود التاجر او كلمة السر و اعادة المحاولة|
   |missing credentials      |                برجاء ادخال كلا من كود التاجر  و كلمة السر|
   |inactive user credentials|     هذا المستخدم غير مفعل برجاء الرجوع الي الجهة المعنية|
