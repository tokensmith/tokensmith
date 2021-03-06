<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Login</title>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>

<div class="main">

    <c:choose>
        <c:when test="${presenter.getUserMessage().isPresent()}">
           <div id="message" class="notification" data-status="ok">
                <p>${presenter.getUserMessage().get()}</p>
           </div>
        </c:when>
    </c:choose>

    <form id="authorization" method="POST">
    <div class="form">
        <input id="email" type="text" name="email" required="true" placeholder="username" value="${presenter.getEmail()}" />
        <input id="password" type="password" name="password" required="true" placeholder="password"/>
        <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getEncodedCsrfToken()}" />
        <button>login</button>

        <p class="follow-link">Not registered? <a href="/register">register</a></p>
        <p class="follow-link">Forgot password? <a href="/forgot-password">reset it</a></p>
    </div>
    </form>
</div

</body>
</html>