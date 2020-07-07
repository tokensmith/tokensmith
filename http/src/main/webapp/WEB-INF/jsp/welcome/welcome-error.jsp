<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Welcome</title>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>
<div class="main">
    <div id="message" class="message" data-status="error">
        <p>Oops, something went wrong. The link may have expired, please login and resend the email verification email.</p>
    </div>
</div>
</body>
</html>