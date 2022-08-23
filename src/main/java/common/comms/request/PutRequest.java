package common.comms.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.api.error.ResponseError;
import common.comms.HttpApiComms;
import common.comms.response.PutResponse;
import org.apache.http.client.methods.HttpPut;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

@JsonIgnoreProperties(value = {"context", "method"})
public interface PutRequest {
    HttpApiComms.HttpMethod method = HttpApiComms.HttpMethod.PUT;

    String getContext();

    default PutResponse send() throws Exception {
        return send(null);
    }

    default PutResponse send(String token) throws Exception {
        String uri = HttpApiComms.getUri(getContext());
        String target = this.getClass().getSimpleName();
        String request = JSONObjectMapper.writeValueAsString(this);
        String res = HttpApiComms.send(new HttpPut(uri), target, token, request);
        try {
            return JSONObjectMapper.readValue(res, PutResponse.class);
        } catch (Exception notPutRes) {
            var err = JSONObjectMapper.readValue(res, ResponseError.class);
            throw err.except();
        }
    }
}
