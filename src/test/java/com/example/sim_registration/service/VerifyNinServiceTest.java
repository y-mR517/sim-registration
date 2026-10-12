package com.example.sim_registration.service;

import com.example.sim_registration.dto.request.VerifyNinRequest;
import com.example.sim_registration.dto.response.VerifyNinResponse;
import com.example.sim_registration.exception.IdentityVerificationFailedException;
import com.example.sim_registration.nida.NidaClient;
import com.example.sim_registration.nida.NidaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyNinServiceTest {

    @Mock private NidaClient nidaClient;

    @InjectMocks private VerifyNinService service;

    private static final String NIN = "11111111111111111111";

    @Test
    void verify_whenNinIsKnown_returnsVerifiedTrue() {
        when(nidaClient.verifyNin(NIN)).thenReturn(
                NidaResponse.builder()
                        .verified(true)
                        .firstName("Test")
                        .lastName("Person")
                        .dateOfBirth("2000-01-01")
                        .build());

        VerifyNinResponse response = service.verify(new VerifyNinRequest(NIN));

        assertTrue(response.isVerified());
    }

    @Test
    void verify_whenNinIsUnknown_throwsIdentityVerificationFailed() {
        when(nidaClient.verifyNin(NIN)).thenReturn(NidaResponse.builder().verified(false).build());

        assertThrows(IdentityVerificationFailedException.class,
                () -> service.verify(new VerifyNinRequest(NIN)));
    }

    //checks that the response class has no field names, dob or the NIN
    @Test
    void response_doesNotCarryIdentityData() {
        for (String field : List.of("nin", "firstName", "lastName", "dateOfBirth")) {
            assertThrows(NoSuchFieldException.class,
                    () -> VerifyNinResponse.class.getDeclaredField(field),
                    "VerifyNinResponse must not contain the field: " + field);
        }
    }
}