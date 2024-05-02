package com.supernova.integrationtests.vo.wrappers

import com.supernova.integrationtests.vo.BookVO
import com.fasterxml.jackson.annotation.JsonProperty

class BookEmbeddedVO {

    @JsonProperty("booksVOList")
    var books: List<BookVO>?= null

}
