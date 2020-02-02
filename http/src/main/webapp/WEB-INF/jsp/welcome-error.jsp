<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Welcome</title>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>
<div class="main">
    <div class="message-container">
        <div id="message" data-status="error">
        Oops, something went wrong. The link may have expired, please login and resend the email verification email.
        </div>
    </div>
</div>
</body>
</html>