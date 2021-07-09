package cl.bci.prueba.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {

    private String number;

    @JsonProperty(value = "citycode")
    private String cityCode;

    @JsonProperty(value = "countrycode")
    private String countryCode;

}
