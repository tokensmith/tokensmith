<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Update Password</title>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>
<div class="main">
    <form id="updatePassword" class="form" method="POST">
        <c:choose>
            <c:when test="${presenter.getErrorMessage().isPresent()}">
               <div id="error" data-status="form-error">
                    ${presenter.getErrorMessage().get()}
               </div>
            </c:when>
        </c:choose>

        <input id="password" type="password" name="password" placeholder="password" required="true"/>
        <input id="repeatPassword" type="password" name="repeatPassword" placeholder="repeat password" required="true"/>

        <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getEncodedCsrfToken()}" />
        <button>Update Password</button>
    </form>
</div>
</body>
</html>