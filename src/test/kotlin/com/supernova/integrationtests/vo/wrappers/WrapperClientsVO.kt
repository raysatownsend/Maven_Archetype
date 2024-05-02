package com.supernova.integrationtests.vo.wrappers

import com.fasterxml.jackson.annotation.JsonProperty

class WrapperClientsVO {

    @JsonProperty("_embedded")
    var embedded: ClientEmbeddedVO? = null

}
