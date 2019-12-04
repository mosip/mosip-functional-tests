#ID-Authentication Partner Demo Serivce
This is a helper service module that can used for ID-Authentication functional testing. This exposes many helper services such as:
1. Creating Auth request for Partner Authentication, Internal Authentication and KYC Authenication request.
2. Encrypt/Decrypt data using Mosip Public Key/Salt/IV, which will internally call the Mosip-Kernel-CryptoManager service.
3. Encode/Decode data - to do base-64-url encoding/decoding
4. Encode/Decode from/to file - to do base-64-url encoding/decoding
5. JWS Sign/Verify

# Create Auth request Service
This service can be used for Partner Authentication, Internal Authentication and KYC Authenication request.
