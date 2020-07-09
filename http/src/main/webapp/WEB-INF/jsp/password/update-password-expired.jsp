<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Update Password</title>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>
<div class="main">
    <div id="message" class="message" data-status="link-expired">
        <p>Oops, looks like this link has expired. Please restart the <a href="${presenter.getForgotPasswordLink()}">forgot password</a> process.</p>
    </div>
</div>
</body>
</html>