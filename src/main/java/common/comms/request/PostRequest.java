package common.comms.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.api.error.ResponseError;
import common.comms.HttpApiComms;
import common.comms.response.PostResponse;
import org.apache.http.client.methods.HttpPost;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

@JsonIgnoreProperties(value = {"context", "method"})
public interface PostRequest {
    HttpApiComms.HttpMethod method = HttpApiComms.HttpMethod.POST;

    String getContext();

    default PostResponse send() throws Exception {
        return send(null);
    }

    default PostResponse send(String token) throws Exception {
        String uri = HttpApiComms.getUri(getContext());
        String target = this.getClass().getSimpleName();
        String request = JSONObjectMapper.writeValueAsString(this);
        String res = HttpApiComms.send(new HttpPost(uri), target, token, request);
        try {
            return JSONObjectMapper.readValue(res, PostResponse.class);
        } catch (Exception notPostRes) {
            var err = JSONObjectMapper.readValue(res, ResponseError.class);
            throw err.except();
        }
    }
}
