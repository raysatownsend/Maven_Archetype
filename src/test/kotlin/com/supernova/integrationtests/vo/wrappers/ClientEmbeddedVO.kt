package com.supernova.integrationtests.vo.wrappers

import com.supernova.integrationtests.vo.ClientVO
import com.fasterxml.jackson.annotation.JsonProperty

class ClientEmbeddedVO {

    @JsonProperty("clientVOList")
    var clients: List<ClientVO>?= null
}
