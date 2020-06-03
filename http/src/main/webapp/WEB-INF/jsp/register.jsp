<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Register</title>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>

<div class="main">
<form id="register" method="POST">
    <div class="form">
    <c:choose>
        <c:when test="${presenter.getErrorMessage().isPresent()}">
           <div id="error">
                ${presenter.getErrorMessage().get()}
           </div>
        </c:when>
    </c:choose>

    <input id="email" type="text" name="email" required="true" placeholder="email" value="${presenter.getEmail()}" />
    <input id="password" type="password" name="password" required="true" placeholder="password"/>
    <input id="repeatPassword" type="password" name="repeatPassword" required="true" placeholder="repeat password"/>

    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getEncodedCsrfToken()}" />
    <button>register</button>
    </div">
</form>
<div class="main">

</body>
</html>