--
-- Confidential Client
--
INSERT INTO public.client (id, redirect_uri) VALUES ('48d4f828-69bc-4e34-81e3-28288fa4de7a', 'https://tokensmith.net');
INSERT INTO public.confidential_client (id, client_id, created_at, active_password, rotate_password) VALUES ('cdeda32a-6a1c-4baa-9c20-2140cc1f4b25', '48d4f828-69bc-4e34-81e3-28288fa4de7a', '2020-01-29 00:55:34.541318+00', '$2a$10$gTHEj1qro8g1elI/vkBGQ.GVLRDXefXXAwUjaSXd49d6FspYt7lxC', NULL);

--
-- Confidential Client has the response types Code and Password.
--
INSERT INTO public.client_response_type (id, client_id, response_type_id) VALUES ('1c77f338-ffb7-4f72-9718-d8e2dd6e475f', '48d4f828-69bc-4e34-81e3-28288fa4de7a', '95bd2346-483c-40dc-991c-3d7aa86440b3');
INSERT INTO public.client_response_type (id, client_id, response_type_id) VALUES ('ba00eb12-d2ee-4512-b12c-365d6a3bac85', '48d4f828-69bc-4e34-81e3-28288fa4de7a', '16a02908-0ffc-40af-96c3-341098f492a1');

--
-- Confidential Client has the scopes: email, profile, openid
--
INSERT INTO public.client_scopes (id, client_id, scope_id) VALUES ('fb3ebd7e-1b3d-486b-b68f-01f3f7214dae', '48d4f828-69bc-4e34-81e3-28288fa4de7a', '886313e1-3b8a-5372-9b90-0c9aee199e5d');
INSERT INTO public.client_scopes (id, client_id, scope_id) VALUES ('db3ebd7e-1b3d-486b-b68f-01f3f7214dae', '48d4f828-69bc-4e34-81e3-28288fa4de7a', 'd9baba84-b7aa-5e73-8d80-67adca38bb6f');
INSERT INTO public.client_scopes (id, client_id, scope_id) VALUES ('ab3ebd7e-1b3d-486b-b68f-01f3f7214dae', '48d4f828-69bc-4e34-81e3-28288fa4de7a', 'ab44da09-2865-594f-b367-ff3933461af1');

--
-- Public Client
--
INSERT INTO public.client (id, redirect_uri) VALUES ('3ea070d8-c687-4ebc-be2f-32dfb1acd372', 'https://tokensmith.net');

--
-- Public Client has the response types TOKEN.
--
INSERT INTO public.client_response_type (id, client_id, response_type_id) VALUES ('8c77f338-ffb7-4f72-9718-d8e2dd6e475f', '3ea070d8-c687-4ebc-be2f-32dfb1acd372', '0b2af677-9712-4c71-a517-5049f6a20526');

--
-- Public Client has the scopes: email, profile, openid
--
INSERT INTO public.client_scopes (id, client_id, scope_id) VALUES ('7d4ec2ce-830c-43a1-a95f-df24ed19b20a', '3ea070d8-c687-4ebc-be2f-32dfb1acd372', '886313e1-3b8a-5372-9b90-0c9aee199e5d');
INSERT INTO public.client_scopes (id, client_id, scope_id) VALUES ('f5d8224c-707d-47db-9d32-e53a838c8152', '3ea070d8-c687-4ebc-be2f-32dfb1acd372', 'd9baba84-b7aa-5e73-8d80-67adca38bb6f');
INSERT INTO public.client_scopes (id, client_id, scope_id) VALUES ('c5fbfc51-0976-4583-94e3-8db63831b83c', '3ea070d8-c687-4ebc-be2f-32dfb1acd372', 'ab44da09-2865-594f-b367-ff3933461af1');