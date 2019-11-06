<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Update Password</title>
</head>
<body>

<form id="updatePassword" method="POST">
    <c:choose>
        <c:when test="${presenter.getErrorMessage().isPresent()}">
           <div id="error" data-status="form-error">
                ${presenter.getErrorMessage().get()}
           </div>
        </c:when>
    </c:choose>

    <label for="password">Password:</label>
    <input id="password" type="password" name="password" required="true"/>

    <label for="repeatPassword">Repeat Password:</label>
    <input id="repeatPassword" type="password" name="repeatPassword" required="true"/>

    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getEncodedCsrfToken()}" />
    <input type="submit"/>
</form>

</body>
</html>