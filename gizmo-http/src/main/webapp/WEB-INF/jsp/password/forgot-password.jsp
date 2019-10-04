<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Forgot Password</title>
</head>
<body>

<form id="forgot" method="POST">
    <c:choose>
        <c:when test="${presenter.getErrorMessage().isPresent()}">
           <div id="error" data-status="form-error">>
                ${presenter.getErrorMessage().get()}
           </div>
        </c:when>
    </c:choose>

    <label for="email">Email:</label>
    <input id="email" type="text" name="email" required="true" value="${presenter.getEmail()}" />

    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getEncodedCsrfToken()}" />
    <input type="submit"/>
</form>

</body>
</html>