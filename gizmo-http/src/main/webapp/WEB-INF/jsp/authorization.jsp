<!DOCTYPE html>
<html lang="en">
<head></head>
<body>

<form id="authorization" method="POST">
    <label for="email">Email:</label>
    <input id="email" type="text" name="email" required="true" value="${presenter.getEmail()}" />

    <label for="password">Password:</label>
    <input id="password" type="password" name="password" required="true"/>
    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getEncodedCsrfToken()}" />
    <input type="submit"/>
</form>

</body>
</html>