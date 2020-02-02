<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Forgot Password</title>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>

<div class="main">
<form id="forgot" method="POST">
<div class="form">
    <c:choose>
        <c:when test="${presenter.getErrorMessage().isPresent()}">
           <div id="error" data-status="form-error">>
                ${presenter.getErrorMessage().get()}
           </div>
        </c:when>
    </c:choose>

    <input id="email" type="text" name="email" required="true" placeholder="email" value="${presenter.getEmail()}" />

    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getEncodedCsrfToken()}" />
    <button>reset password</button>
</div>
</form>
</div>

</body>
</html>