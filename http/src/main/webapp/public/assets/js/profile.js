
document.onreadystatechange = function () {
    if (document.readyState == "interactive") {
        // set defaults for axios
        const utils = new DomUtils();
        var csrfToken = utils.metaContent('csrf-token');

        axios.defaults.headers.common['X-CSRF'] = csrfToken;

        axios.defaults.headers.post['Content-Type'] = 'application/json; charset=utf-8;';
        axios.defaults.headers.post['Accept'] = 'application/json; charset=utf-8;';

        axios.defaults.headers.put['Content-Type'] = 'application/json; charset=utf-8;';
        axios.defaults.headers.put['Accept'] = 'application/json; charset=utf-8;';

        axios.defaults.headers.delete['Content-Type'] = 'application/json; charset=utf-8;';
        axios.defaults.headers.delete['Accept'] = 'application/json; charset=utf-8;';
    }
}

document.addEventListener('click', function (event) {

    // there's probably a better way to manage dependencies.
    // for now this is ok.
    const utils = new DomUtils();
    const rest = new Rest(axios);
    const profile = new Profile(utils, rest);
    const address = new Address(utils, rest);

    var isRest = event.target.dataset.rest;
    if (isRest == "true") {
        var verb = event.target.dataset.verb
        var resource = event.target.dataset.resource

        if (resource == 'profile' && verb == 'put') {
            profile.update(event);
        } else if (resource == 'address' && verb == 'put') {
            address.update(event);
        } else if (resource == 'address' && verb == 'post') {
            address.create(event);
        } else if (resource == 'address' && verb == 'delete') {
            address.delete(event);
        }
    } else {
        if (event.target.matches("#profile-extra-more")) {
            profile.extraMoreClick();
        } else if (event.target.matches("#profile-extra-less")) {
            profile.extraLessClick();
        }
    }
})

class Profile {
    constructor(utils, rest) {
        this.utils = utils;
        this.rest = rest;
    }

    update(event) {
        event.preventDefault();

        this._disableButton(true);

        var form = event.target.parentElement;
        var profile = this.utils.formToDict(form);

        var profileId = profile['id'];

        // family name
        var familyNameId = profile['family_name_id'];
        var familyName = profile['family_name'];
        profile['family_name'] = { 'id': familyNameId, 'profile_id': profileId, 'name': familyName};

        // given name
        var givenNameId = profile['given_name_id'];
        var givenName = profile['given_name'];
        profile['given_name'] = { 'id': givenNameId, 'profile_id': profileId, 'name': givenName};

        delete profile['family_name_id'];
        delete profile['given_name_id'];

        var payload = JSON.stringify(profile);

        var cbConfig = {
            verb: "PUT",
            resource: "Profile",
            element: event.target.parentElement
        }
        this.rest.exec('PUT', '/api/site/v1/profile', payload, this.onSuccess, this.onError, cbConfig);
        this._disableButton(false);
    }

    _disableButton(disabled) {
        document.getElementById("profile-form-bttn").disabled = disabled;
    }

    extraMoreClick() {
        // show extra input items.
        var profileExtra = document.getElementById("profile-extra");
        profileExtra.classList.remove("hide");

        // hide more call to action
        var profileExtraMoreLink = document.getElementById("profile-extra-more");
        profileExtraMoreLink.classList.add("hide");

        // show less call to action.
        var profileExtraLessLink = document.getElementById("profile-extra-less");
        profileExtraLessLink.classList.remove("hide");
    }

    extraLessClick() {
        var profileExtra = document.getElementById("profile-extra");
        profileExtra.classList.add("hide");

        // show more call to action
        var profileExtraMoreLink = document.getElementById("profile-extra-more");
        profileExtraMoreLink.classList.remove("hide");

        // hide less call to action.
        var profileExtraLessLink = document.getElementById("profile-extra-less");
        profileExtraLessLink.classList.add("hide");
    }

    onSuccess(response, config) {
        console.log(response);

        var givenNameId = null
        if (response.data.given_name != null) {
            givenNameId = response.data.given_name.id;
        }

        var familyNameId = null
        if (response.data.family_name != null) {
            familyNameId = response.data.family_name.id;
        }

        document.getElementById("givenNameId").value = givenNameId;
        document.getElementById("familyNameId").value = familyNameId;
        // 162: say its been updated.
    }

    onError(error, config) {
        console.log(error);
        // 162: say there was an error.
    }
}

class Address {
    constructor(utils, rest) {
        this.utils = utils;
        this.rest = rest;
    }

    create(event) {
        event.preventDefault();

        var form = event.target.parentElement;
        var address = this.utils.formToDict(form);

        var payload = JSON.stringify(address);
        var path = '/api/site/v1/profile/' + address.profile_id + '/address';
        var cbConfig = {
            verb: "POST",
            resource: "Address",
            element: event.target.parentElement
        }
        this.rest.exec('POST', path, payload, this.onSuccess, this.onError, cbConfig);
    }

    update(event) {
        event.preventDefault();

        var form = event.target.parentElement;
        var address = this.utils.formToDict(form);

        var payload = JSON.stringify(address);
        var path = '/api/site/v1/profile/' + address.profile_id + '/address/' + address.id;

        var cbConfig = {
            verb: "PUT",
            resource: "Address",
            element: event.target.parentElement
        }
        this.rest.exec('PUT', path, payload, this.onSuccess, this.onError, cbConfig);
    }

    delete(event) {
        event.preventDefault();

        var form = event.target.parentElement;
        var address = this.utils.formToDict(form);

        var path = '/api/site/v1/profile/' + address.profile_id + '/address/' + address.id;
        var cbConfig = {
            verb: "DELETE",
            resource: "Address",
            element: event.target.parentElement
        }
        this.rest.exec('DELETE', path, null, this.onSuccess, this.onError, cbConfig);
    }

    onSuccess(response, config) {
        if (config['verb'] == 'POST') {

            config.element.setAttribute('id', 'addr-form-put-' + response.data.id)
            config.element.children.id.value = response.data.id;

            // remove button to add address and add buttons to update and delete
            for (var i = 0; i < config.element.elements.length; i++) {
              var nodeName = config.element.elements[i].nodeName;
              var nodeId = config.element.elements[i].id
              if (nodeName === "BUTTON" && nodeId == "addr-bttn-post") {
                config.element.elements[i].remove();
              } else if (nodeName === "BUTTON" && nodeId == "addr-bttn-put") {
                config.element.elements[i].setAttribute("id", "addr-bttn-put-" + response.data.id);
                config.element.elements[i].classList.remove("hide");
              } else if (nodeName === "BUTTON" && nodeId == "addr-bttn-delete") {
                config.element.elements[i].setAttribute("id", "addr-bttn-delete-" + response.data.id);
                config.element.elements[i].classList.remove("hide");
              }
            }

            // add a new address form
            var form = document.getElementById("addr-form-hidden").cloneNode(true);
            form.setAttribute("id", "addr-bttn-post");
            form.classList.remove("hide");
            config.element.parentElement.appendChild(form);

        } else if (config['verb'] == 'PUT') {
            // do nothing
        } else if (config['verb'] == 'DELETE') {
           config.element.remove();
        }
        console.log(response);
    }

    onError(error, config) {
        console.log(error);
    }
}

class DomUtils {

    metaContent(name) {
        return document.querySelector('meta[name="' + name + '"]').content
    }

    formToDict(formData) {
        var formData = new FormData(formData);
        var object = {};
        formData.forEach(function(value, key){
            object[key] = value;
        });
        return object;
    }
}

class Rest {
    constructor(axios) {
        this.axios = axios;
    }

    exec(method, url, payload, onSuccess, onError, cbConfig) {
        this.axios({
          method: method,
          url: url,
          data: payload,
          withCredentials: false
        }).
        then(function (response) {
          onSuccess(response, cbConfig);
        })
        .catch(function (error) {
          onError(error, cbConfig);
        });
    }
}