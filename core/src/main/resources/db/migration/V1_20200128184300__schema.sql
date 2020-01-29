--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5 (Debian 11.5-1.pgdg90+1)
-- Dumped by pg_dump version 11.5 (Debian 11.5-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: access_request; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.access_request (
    id uuid NOT NULL,
    redirect_uri character varying(254),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_id uuid NOT NULL,
    resource_owner_id uuid NOT NULL,
    nonce character varying(1024)
);


ALTER TABLE public.access_request OWNER TO postgres;

--
-- Name: access_request_scopes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.access_request_scopes (
    id uuid NOT NULL,
    access_request_id uuid NOT NULL,
    scope_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.access_request_scopes OWNER TO postgres;

--
-- Name: auth_code; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.auth_code (
    id uuid NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    access_request_id uuid,
    revoked boolean DEFAULT false,
    active_code character varying(1024) NOT NULL,
    rotate_code character varying(1024)
);


ALTER TABLE public.auth_code OWNER TO postgres;

--
-- Name: auth_code_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.auth_code_token (
    id uuid NOT NULL,
    auth_code_id uuid NOT NULL,
    token_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.auth_code_token OWNER TO postgres;

--
-- Name: client; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.client (
    id uuid NOT NULL,
    redirect_uri character varying(254),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.client OWNER TO postgres;

--
-- Name: client_response_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.client_response_type (
    id uuid NOT NULL,
    client_id uuid NOT NULL,
    response_type_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.client_response_type OWNER TO postgres;

--
-- Name: client_scopes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.client_scopes (
    id uuid NOT NULL,
    client_id uuid NOT NULL,
    scope_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.client_scopes OWNER TO postgres;

--
-- Name: confidential_client; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.confidential_client (
    id uuid NOT NULL,
    client_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    active_password character varying(1024) NOT NULL,
    rotate_password character varying(1024)
);


ALTER TABLE public.confidential_client OWNER TO postgres;

--
-- Name: configuration; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.configuration (
    id uuid NOT NULL,
    access_token_size integer NOT NULL,
    authorization_code_size integer NOT NULL,
    refresh_token_size integer NOT NULL,
    access_token_code_seconds_to_expiry integer NOT NULL,
    access_token_token_seconds_to_expiry integer NOT NULL,
    access_token_password_seconds_to_expiry integer NOT NULL,
    access_token_refresh_seconds_to_expiry integer NOT NULL,
    access_token_client_seconds_to_expiry integer NOT NULL,
    authorization_code_seconds_to_expiry integer NOT NULL,
    refresh_token_seconds_to_expiry integer NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.configuration OWNER TO postgres;

--
-- Name: nonce; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nonce (
    id uuid NOT NULL,
    nonce_type_id uuid NOT NULL,
    resource_owner_id uuid NOT NULL,
    revoked boolean DEFAULT false NOT NULL,
    spent boolean DEFAULT false NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    active_nonce character varying(1024) NOT NULL,
    rotate_nonce character varying(1024)
);


ALTER TABLE public.nonce OWNER TO postgres;

--
-- Name: nonce_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nonce_type (
    id uuid NOT NULL,
    name character varying(254),
    seconds_to_expiry integer DEFAULT 86400 NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.nonce_type OWNER TO postgres;

--
-- Name: refresh_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.refresh_token (
    id uuid NOT NULL,
    token_id uuid NOT NULL,
    revoked boolean DEFAULT false,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    active_token character varying(1024) NOT NULL,
    rotate_token character varying(1024)
);


ALTER TABLE public.refresh_token OWNER TO postgres;

--
-- Name: resource_owner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resource_owner (
    id uuid NOT NULL,
    email character varying(245) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    email_verified boolean DEFAULT false NOT NULL,
    active_password character varying(1024) NOT NULL,
    rotate_password character varying(1024)
);


ALTER TABLE public.resource_owner OWNER TO postgres;

--
-- Name: resource_owner_profile; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resource_owner_profile (
    id uuid NOT NULL,
    resource_owner_id uuid NOT NULL,
    name character varying(245),
    middle_name character varying(245),
    nick_name character varying(245),
    preferred_user_name character varying(245),
    profile character varying(245),
    picture character varying(245),
    website character varying(245),
    gender character varying(7),
    birth_date timestamp with time zone,
    zone_info character varying(50),
    locale character varying(50),
    phone_number character varying(245),
    phone_number_verified boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.resource_owner_profile OWNER TO postgres;

--
-- Name: resource_owner_profile_address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resource_owner_profile_address (
    id uuid NOT NULL,
    resource_owner_profile_id uuid NOT NULL,
    street_address character varying(245) NOT NULL,
    street_address2 character varying(245),
    locality character varying(100) NOT NULL,
    region character varying(100) NOT NULL,
    postal_code character varying(50) NOT NULL,
    country character varying(50) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.resource_owner_profile_address OWNER TO postgres;

--
-- Name: resource_owner_profile_family_name; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resource_owner_profile_family_name (
    id uuid NOT NULL,
    resource_owner_profile_id uuid NOT NULL,
    family_name character varying(50) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.resource_owner_profile_family_name OWNER TO postgres;

--
-- Name: resource_owner_profile_given_name; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resource_owner_profile_given_name (
    id uuid NOT NULL,
    resource_owner_profile_id uuid NOT NULL,
    given_name character varying(50) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.resource_owner_profile_given_name OWNER TO postgres;

--
-- Name: resource_owner_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resource_owner_token (
    id uuid NOT NULL,
    resource_owner_id uuid NOT NULL,
    token_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.resource_owner_token OWNER TO postgres;

--
-- Name: response_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.response_type (
    id uuid NOT NULL,
    name character varying(100) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.response_type OWNER TO postgres;

--
-- Name: rsa_private_key; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rsa_private_key (
    id uuid NOT NULL,
    use character varying(245) NOT NULL,
    modulus bytea NOT NULL,
    public_exponent bytea NOT NULL,
    private_exponent bytea NOT NULL,
    prime_p bytea NOT NULL,
    prime_q bytea NOT NULL,
    prime_exponent_p bytea NOT NULL,
    prime_exponent_q bytea NOT NULL,
    crt_coefficient bytea NOT NULL,
    active boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.rsa_private_key OWNER TO postgres;

--
-- Name: scope; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.scope (
    id uuid NOT NULL,
    name character varying(254),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.scope OWNER TO postgres;

--
-- Name: token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token (
    id uuid NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    revoked boolean DEFAULT false,
    grant_type character varying(100) NOT NULL,
    client_id uuid NOT NULL,
    active_token character varying(1024) NOT NULL,
    rotate_token character varying(1024),
    nonce character varying(1024),
    lead_auth_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.token OWNER TO postgres;

--
-- Name: token_audience; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token_audience (
    id uuid NOT NULL,
    client_id uuid NOT NULL,
    token_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.token_audience OWNER TO postgres;

--
-- Name: token_chain; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token_chain (
    id uuid NOT NULL,
    token_id uuid NOT NULL,
    prev_token_id uuid NOT NULL,
    refresh_token_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.token_chain OWNER TO postgres;

--
-- Name: token_lead_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token_lead_token (
    id uuid NOT NULL,
    token_id uuid NOT NULL,
    lead_token_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.token_lead_token OWNER TO postgres;

--
-- Name: token_scope; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token_scope (
    id uuid NOT NULL,
    token_id uuid NOT NULL,
    scope_id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.token_scope OWNER TO postgres;

--
-- Name: access_request access_request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.access_request
    ADD CONSTRAINT access_request_pkey PRIMARY KEY (id);


--
-- Name: access_request_scopes access_request_scopes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.access_request_scopes
    ADD CONSTRAINT access_request_scopes_pkey PRIMARY KEY (id);


--
-- Name: auth_code auth_code_active_code_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth_code
    ADD CONSTRAINT auth_code_active_code_unique UNIQUE (active_code);


--
-- Name: auth_code_token auth_code_id_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth_code_token
    ADD CONSTRAINT auth_code_id_unique UNIQUE (auth_code_id);


--
-- Name: auth_code auth_code_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth_code
    ADD CONSTRAINT auth_code_pkey PRIMARY KEY (id);


--
-- Name: auth_code_token auth_code_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth_code_token
    ADD CONSTRAINT auth_code_token_pkey PRIMARY KEY (id);


--
-- Name: client client_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT client_pkey PRIMARY KEY (id);


--
-- Name: client_response_type client_response_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_response_type
    ADD CONSTRAINT client_response_type_pkey PRIMARY KEY (id);


--
-- Name: client_scopes client_scopes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_scopes
    ADD CONSTRAINT client_scopes_pkey PRIMARY KEY (id);


--
-- Name: token_audience client_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_audience
    ADD CONSTRAINT client_token_pkey PRIMARY KEY (id);


--
-- Name: confidential_client confidential_client_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.confidential_client
    ADD CONSTRAINT confidential_client_pkey PRIMARY KEY (id);


--
-- Name: configuration configuration_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (id);


--
-- Name: response_type name_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.response_type
    ADD CONSTRAINT name_unique UNIQUE (name);


--
-- Name: nonce nonce_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nonce
    ADD CONSTRAINT nonce_pkey PRIMARY KEY (id);


--
-- Name: nonce_type nonce_type_name_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nonce_type
    ADD CONSTRAINT nonce_type_name_unique UNIQUE (name);


--
-- Name: nonce_type nonce_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nonce_type
    ADD CONSTRAINT nonce_type_pkey PRIMARY KEY (id);


--
-- Name: refresh_token refresh_token_active_token_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT refresh_token_active_token_unique UNIQUE (active_token);


--
-- Name: refresh_token refresh_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT refresh_token_pkey PRIMARY KEY (id);


--
-- Name: resource_owner resource_owner_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner
    ADD CONSTRAINT resource_owner_email_key UNIQUE (email);


--
-- Name: resource_owner resource_owner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner
    ADD CONSTRAINT resource_owner_pkey PRIMARY KEY (id);


--
-- Name: resource_owner_profile_address resource_owner_profile_address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile_address
    ADD CONSTRAINT resource_owner_profile_address_pkey PRIMARY KEY (id);


--
-- Name: resource_owner_profile_family_name resource_owner_profile_family_name_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile_family_name
    ADD CONSTRAINT resource_owner_profile_family_name_pkey PRIMARY KEY (id);


--
-- Name: resource_owner_profile_given_name resource_owner_profile_given_name_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile_given_name
    ADD CONSTRAINT resource_owner_profile_given_name_pkey PRIMARY KEY (id);


--
-- Name: resource_owner_profile resource_owner_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile
    ADD CONSTRAINT resource_owner_profile_pkey PRIMARY KEY (id);


--
-- Name: resource_owner_token resource_owner_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_token
    ADD CONSTRAINT resource_owner_token_pkey PRIMARY KEY (id);


--
-- Name: response_type response_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.response_type
    ADD CONSTRAINT response_type_pkey PRIMARY KEY (id);


--
-- Name: rsa_private_key rsa_private_key_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rsa_private_key
    ADD CONSTRAINT rsa_private_key_pkey PRIMARY KEY (id);


--
-- Name: scope scope_name_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scope
    ADD CONSTRAINT scope_name_unique UNIQUE (name);


--
-- Name: scope scope_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scope
    ADD CONSTRAINT scope_pkey PRIMARY KEY (id);


--
-- Name: token token_active_token_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_active_token_unique UNIQUE (active_token);


--
-- Name: token_chain token_chain_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_chain
    ADD CONSTRAINT token_chain_pkey PRIMARY KEY (id);


--
-- Name: token_chain token_chain_refresh_token_id_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_chain
    ADD CONSTRAINT token_chain_refresh_token_id_unique UNIQUE (refresh_token_id);


--
-- Name: token_lead_token token_lead_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_lead_token
    ADD CONSTRAINT token_lead_token_pkey PRIMARY KEY (id);


--
-- Name: token token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_pkey PRIMARY KEY (id);


--
-- Name: token_scope token_scope_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_scope
    ADD CONSTRAINT token_scope_pkey PRIMARY KEY (id);


--
-- Name: active_and_use_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX active_and_use_idx ON public.rsa_private_key USING btree (active, use);


--
-- Name: nonce_expires_at; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nonce_expires_at ON public.nonce USING btree (expires_at);


--
-- Name: nonce_revoked; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nonce_revoked ON public.nonce USING btree (revoked);


--
-- Name: nonce_spent; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nonce_spent ON public.nonce USING btree (spent);


--
-- Name: refresh_token_expires_at; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX refresh_token_expires_at ON public.refresh_token USING btree (expires_at);


--
-- Name: refresh_token_revoked; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX refresh_token_revoked ON public.refresh_token USING btree (revoked);


--
-- Name: token_expires_at; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX token_expires_at ON public.token USING btree (expires_at);


--
-- Name: token_revoked; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX token_revoked ON public.token USING btree (revoked);


--
-- Name: access_request access_request_client_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.access_request
    ADD CONSTRAINT access_request_client_uuid_fkey FOREIGN KEY (client_id) REFERENCES public.client(id);


--
-- Name: access_request access_request_resource_owner_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.access_request
    ADD CONSTRAINT access_request_resource_owner_uuid_fkey FOREIGN KEY (resource_owner_id) REFERENCES public.resource_owner(id);


--
-- Name: access_request_scopes access_request_scopes_access_request_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.access_request_scopes
    ADD CONSTRAINT access_request_scopes_access_request_uuid_fkey FOREIGN KEY (access_request_id) REFERENCES public.access_request(id);


--
-- Name: access_request_scopes access_request_scopes_scope_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.access_request_scopes
    ADD CONSTRAINT access_request_scopes_scope_uuid_fkey FOREIGN KEY (scope_id) REFERENCES public.scope(id);


--
-- Name: auth_code auth_code_access_request_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth_code
    ADD CONSTRAINT auth_code_access_request_uuid_fkey FOREIGN KEY (access_request_id) REFERENCES public.access_request(id);


--
-- Name: auth_code_token auth_code_token_auth_code_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth_code_token
    ADD CONSTRAINT auth_code_token_auth_code_id_fkey FOREIGN KEY (auth_code_id) REFERENCES public.auth_code(id);


--
-- Name: auth_code_token auth_code_token_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth_code_token
    ADD CONSTRAINT auth_code_token_token_id_fkey FOREIGN KEY (token_id) REFERENCES public.token(id);


--
-- Name: client_response_type client_response_type_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_response_type
    ADD CONSTRAINT client_response_type_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.client(id);


--
-- Name: client_response_type client_response_type_response_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_response_type
    ADD CONSTRAINT client_response_type_response_type_id_fkey FOREIGN KEY (response_type_id) REFERENCES public.response_type(id);


--
-- Name: client_scopes client_scopes_client_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_scopes
    ADD CONSTRAINT client_scopes_client_uuid_fkey FOREIGN KEY (client_id) REFERENCES public.client(id);


--
-- Name: client_scopes client_scopes_scope_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_scopes
    ADD CONSTRAINT client_scopes_scope_uuid_fkey FOREIGN KEY (scope_id) REFERENCES public.scope(id);


--
-- Name: token_audience client_token_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_audience
    ADD CONSTRAINT client_token_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.client(id);


--
-- Name: token_audience client_token_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_audience
    ADD CONSTRAINT client_token_token_id_fkey FOREIGN KEY (token_id) REFERENCES public.token(id);


--
-- Name: confidential_client confidential_client_client_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.confidential_client
    ADD CONSTRAINT confidential_client_client_uuid_fkey FOREIGN KEY (client_id) REFERENCES public.client(id);


--
-- Name: nonce nonce_nonce_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nonce
    ADD CONSTRAINT nonce_nonce_type_id_fkey FOREIGN KEY (nonce_type_id) REFERENCES public.nonce_type(id);


--
-- Name: nonce nonce_resource_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nonce
    ADD CONSTRAINT nonce_resource_owner_id_fkey FOREIGN KEY (resource_owner_id) REFERENCES public.resource_owner(id);


--
-- Name: refresh_token refresh_token_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT refresh_token_token_id_fkey FOREIGN KEY (token_id) REFERENCES public.token(id);


--
-- Name: resource_owner_profile_address resource_owner_profile_address_resource_owner_profile_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile_address
    ADD CONSTRAINT resource_owner_profile_address_resource_owner_profile_id_fkey FOREIGN KEY (resource_owner_profile_id) REFERENCES public.resource_owner_profile(id);


--
-- Name: resource_owner_profile_family_name resource_owner_profile_family_na_resource_owner_profile_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile_family_name
    ADD CONSTRAINT resource_owner_profile_family_na_resource_owner_profile_id_fkey FOREIGN KEY (resource_owner_profile_id) REFERENCES public.resource_owner_profile(id);


--
-- Name: resource_owner_profile_given_name resource_owner_profile_given_nam_resource_owner_profile_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile_given_name
    ADD CONSTRAINT resource_owner_profile_given_nam_resource_owner_profile_id_fkey FOREIGN KEY (resource_owner_profile_id) REFERENCES public.resource_owner_profile(id);


--
-- Name: resource_owner_profile resource_owner_profile_resource_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_profile
    ADD CONSTRAINT resource_owner_profile_resource_owner_id_fkey FOREIGN KEY (resource_owner_id) REFERENCES public.resource_owner(id);


--
-- Name: resource_owner_token resource_owner_token_resource_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_token
    ADD CONSTRAINT resource_owner_token_resource_owner_id_fkey FOREIGN KEY (resource_owner_id) REFERENCES public.resource_owner(id);


--
-- Name: resource_owner_token resource_owner_token_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resource_owner_token
    ADD CONSTRAINT resource_owner_token_token_id_fkey FOREIGN KEY (token_id) REFERENCES public.token(id);


--
-- Name: token_chain token_chain_prev_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_chain
    ADD CONSTRAINT token_chain_prev_token_id_fkey FOREIGN KEY (prev_token_id) REFERENCES public.token(id);


--
-- Name: token_chain token_chain_refresh_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_chain
    ADD CONSTRAINT token_chain_refresh_token_id_fkey FOREIGN KEY (refresh_token_id) REFERENCES public.refresh_token(id);


--
-- Name: token_chain token_chain_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_chain
    ADD CONSTRAINT token_chain_token_id_fkey FOREIGN KEY (token_id) REFERENCES public.token(id);


--
-- Name: token token_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.client(id);


--
-- Name: token_lead_token token_lead_token_lead_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_lead_token
    ADD CONSTRAINT token_lead_token_lead_token_id_fkey FOREIGN KEY (lead_token_id) REFERENCES public.token(id);


--
-- Name: token_lead_token token_lead_token_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_lead_token
    ADD CONSTRAINT token_lead_token_token_id_fkey FOREIGN KEY (token_id) REFERENCES public.token(id);


--
-- Name: token_scope token_scope_scope_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_scope
    ADD CONSTRAINT token_scope_scope_id_fkey FOREIGN KEY (scope_id) REFERENCES public.scope(id);


--
-- Name: token_scope token_scope_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_scope
    ADD CONSTRAINT token_scope_token_id_fkey FOREIGN KEY (token_id) REFERENCES public.token(id);


--
-- PostgreSQL database dump complete
--

