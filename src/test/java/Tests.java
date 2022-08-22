import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.* ;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

public class Tests {
    @ParameterizedTest
    @MethodSource("bloodPressureSource")
    public void checkBloodPressureTest (int expected) {
        //arrange
        BloodPressure normalPressure = new BloodPressure(120, 70);
        PatientInfo info = Mockito.mock(PatientInfo.class);
        PatientInfoRepository infoRep = Mockito.mock(PatientInfoFileRepository.class);
        String message = String.format("Warning, patient with id: %s, need help", info.getId());
        SendAlertService sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl
                (infoRep, sendAlertService);
        Mockito.when(infoRep.getById(info.getId())).thenReturn(info);
        Mockito.when(info.getHealthInfo()).thenReturn(Mockito.mock(HealthInfo.class));
        switch (expected) {
            case 0:
                Mockito.when(info.getHealthInfo().getBloodPressure()).thenReturn(normalPressure);
                break;
            case 1:
                Mockito.when(info.getHealthInfo().getBloodPressure()).thenReturn(new BloodPressure(180, 90));
                break;
        }
        //act
        medicalService.checkBloodPressure(info.getId(), normalPressure);
        //assert
        Mockito.verify(sendAlertService, Mockito.times(expected)).send(message);

    }
    @ParameterizedTest
    @MethodSource("source")
    public void checkTemperatureTest(int expected){
        //arrange
        BigDecimal temperature = new BigDecimal("36.6");
        PatientInfo info = Mockito.mock(PatientInfo.class);
        PatientInfoRepository infoRep = Mockito.mock(PatientInfoFileRepository.class);
        String message = String.format("Warning, patient with id: %s, need help", info.getId());
        SendAlertService sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl
                (infoRep, sendAlertService);
        Mockito.when(infoRep.getById(info.getId())).thenReturn(info);
        Mockito.when(info.getHealthInfo()).thenReturn(Mockito.mock(HealthInfo.class));
        switch (expected) {
            case 0:
                Mockito.when(info.getHealthInfo().getNormalTemperature()).thenReturn(temperature);
                break;
            case 1:
                Mockito.when(info.getHealthInfo().getNormalTemperature()).thenReturn(new BigDecimal("38.9"));
                break;
        }
        //act
        medicalService.checkTemperature(info.getId(), temperature);
        //assert
        Mockito.verify(sendAlertService, Mockito.times(expected)).send(message);
    }

    public static Stream<Arguments> source (){
        return Stream.of(
                Arguments.of(0),
                Arguments.of(1)

        );
    }
}
