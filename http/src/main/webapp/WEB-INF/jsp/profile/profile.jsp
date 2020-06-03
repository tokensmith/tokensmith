<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Profile</title>
<meta name="csrf-token" content="${presenter.getEncodedCsrfToken()}">
<script src="/assets/js/lib/axios.min.js"></script>
<script src="/assets/js/profile.js"></script>
<link rel="stylesheet" type="text/css" href="${presenter.getGlobalCssPath()}">
</head>
<body>

<form id="profile-form" class="form profile-form">
    <c:choose>
        <c:when test="${presenter.getErrorMessage().isPresent()}">
           <div id="error" data-status="form-error">>
                ${presenter.getErrorMessage().get()}
           </div>
        </c:when>
    </c:choose>

    <input id="id" type="hidden" name="id" required="true" value="${presenter.getId()}" />
    <input id="resourceOwnerId" type="hidden" name="resource_owner_id" required="true" value="${presenter.getResourceOwnerId()}" />

    <label for="email">Email:</label>
    <input id="email" type="email" name="email" required="true" value="${presenter.getEmail()}" />

    <label for="phoneNumber">Phone:</label>
    <input id="phoneNumber" type="tel" name="phone_number" value="${presenter.getPhoneNumber()}" />

    <input id="givenNameId" type="hidden" name="given_name_id" value="${presenter.getGivenNameId()}" />

    <label for="givenName">Given Name:</label>
    <input id="givenName" type="text" name="given_name" value="${presenter.getGivenName()}" />

    <input id="familyNameId" type="hidden" name="family_name_id" value="${presenter.getFamilyNameId()}" />

    <label for="familyName">Last Name:</label>
    <input id="familyName" type="text" name="family_name" value="${presenter.getFamilyName()}" />

    <label for="birthDate">Birth Date:</label>
    <input id="birthDate" type="date" name="birth_date" value="${presenter.getBirthDate()}" />

    <div id="profile-extra" class="hide">

    <!-- 162: do we need name? -->
    <label for="name">First Name:</label>
    <input id="name" type="text" name="name" value="${presenter.getName()}" />

    <label for="middleName">Middle Name:</label>
    <input id="middleName" type="text" name="middle_name" value="${presenter.getMiddleName()}" />

    <label for="nickName">Nick Name:</label>
    <input id="nickName" type="text" name="nick_name" value="${presenter.getNickName()}" />

    <label for="preferredUserName">Preferred User Name:</label>
    <input id="preferredUserName" type="text" name="preferred_user_name" value="${presenter.getPreferredUserName()}" />

    <label for="profile">Profile URL:</label>
    <input id="profile" type="url" name="profile" value="${presenter.getProfile()}" />

    <label for="picture">Picture URL:</label>
    <input id="picture" type="url" name="picture" value="${presenter.getPicture()}" />

    <label for="website">Website:</label>
    <input id="website" type="url" name="website" value="${presenter.getWebsite()}" />

    <label for="gender">Gender:</label>
    <input id="gender" type="text" name="gender" value="${presenter.getGender()}" />

    <label for="zoneInfo">TimeZone:</label>
    <input id="zoneInfo" type="text" name="zone_info" value="${presenter.getZoneInfo()}" />

    <label for="locale">Locale:</label>
    <input id="locale" type="text" name="locale" value="${presenter.getLocale()}" />
    </div>

    <button id="profile-form-bttn" data-rest="true" data-verb="put" data-resource="profile">submit</button>
    <p class="message"><a id="profile-extra-more" href="#">more</a></p>
    <p class="message"><a id="profile-extra-less" class="hide" href="#">less</a></p>
</form>

    <c:forEach items="${presenter.getAddresses()}" var="addr">
        <form id="addr-form-put-${addr.getId()}" class="form profile-form">
            <input id="id" type="hidden" name="id" required="true" value="${addr.getId()}" />
            <input id="profileId" type="hidden" name="profile_id" required="true" value="${presenter.getId()}" />

            <label for="streetAddress">Address 1:</label>
            <input id="streetAddress" type="text" name="street_address" value="${addr.getStreetAddress()}" />

            <label for="streetAddress2">Address 2:</label>
            <input id="streetAddress2" type="text" name="street_address2" value="${addr.getStreetAddress2().get()}" />

            <label for="locality">Locality:</label>
            <input id="locality" type="text" name="locality" value="${addr.getLocality()}" />

            <label for="region">Region:</label>
            <input id="region" type="text" name="region" value="${addr.getRegion()}" />

            <label for="postalCode">Postal Code:</label>
            <input id="postalCode" type="text" name="postal_code" value="${addr.getPostalCode()}" />

            <label for="country">Country:</label>
            <input id="country" type="text" name="country" value="${addr.getCountry()}" />

            <button id="addr-bttn-put-${addr.getId()}" data-rest="true" data-verb="put" data-resource="address">Update Address</button>
            <button id="addr-bttn-delete-${addr.getId()}" data-rest="true" data-verb="delete" data-resource="address">Delete Address</button>
        </form>
    </c:forEach>

    <form id="addr-form-post" class="form profile-form">
        <input id="id" type="hidden" name="id"/>
        <input id="profileId" type="hidden" name="profile_id" required="true" value="${presenter.getId()}" />

        <label for="streetAddress">Address 1:</label>
        <input id="streetAddress" type="text" name="street_address" required/>

        <label for="streetAddress2">Address 2:</label>
        <input id="streetAddress2" type="text" name="street_address2"/>

        <label for="locality">Locality:</label>
        <input id="locality" type="text" name="locality"/>

        <label for="region">Region:</label>
        <input id="region" type="text" name="region" required/>

        <label for="postalCode">Postal Code:</label>
        <input id="postalCode" type="text" name="postal_code" required/>

        <label for="country">Country:</label>
        <input id="country" type="text" name="country"/>

        <button id="addr-bttn-put" class="hide" data-rest="true" data-verb="put" data-resource="address">Update Address</button>
        <button id="addr-bttn-delete" class="hide" data-rest="true" data-verb="delete" data-resource="address">Delete Address</button>
        <button id="addr-bttn-post" data-rest="true" data-verb="post" data-resource="address">Add Address</button>
    </form>


    <!-- used in javascript to clone and add a new form -->
    <form id="addr-form-hidden" class="form profile-form hide">
        <input id="id" type="hidden" name="id"/>
        <input id="profileId" type="hidden" name="profile_id" required="true" value="${presenter.getId()}" />

        <label for="streetAddress">Address 1:</label>
        <input id="streetAddress" type="text" name="street_address" required/>

        <label for="streetAddress2">Address 2:</label>
        <input id="streetAddress2" type="text" name="street_address2"/>

        <label for="locality">Locality:</label>
        <input id="locality" type="text" name="locality"/>

        <label for="region">Region:</label>
        <input id="region" type="text" name="region" required/>

        <label for="postalCode">Postal Code:</label>
        <input id="postalCode" type="text" name="postal_code" required/>

        <label for="country">Country:</label>
        <input id="country" type="text" name="country"/>

        <button id="addr-bttn-put" class="hide" data-rest="true" data-verb="put" data-resource="address">Update Address</button>
        <button id="addr-bttn-delete" class="hide" data-rest="true" data-verb="delete" data-resource="address">Delete Address</button>
        <button id="addr-bttn-post" data-rest="true" data-verb="post" data-resource="address">Add Address</button>
    </form>


</body>
</html>