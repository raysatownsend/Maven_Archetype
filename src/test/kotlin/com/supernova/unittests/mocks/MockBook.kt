package com.supernova.unittests.mocks

import com.supernova.data.vo.v1.AuthorVO
import com.supernova.model.Author

class MockBook {
    fun mockEntity(): Author {
        return mockEntity(0)
    }

    fun mockVO(): AuthorVO {
        return mockVO(0)
    }

    fun mockEntityList(): ArrayList<Author> {
        val authors: ArrayList<Author> = ArrayList()
        for (i in 0..13) {
            authors.add(mockEntity(i))
        }
        return authors
    }

    fun mockVOList(): ArrayList<AuthorVO> {
        val authors: ArrayList<AuthorVO> = ArrayList()
        for (i in 0..13) {
            authors.add(mockVO(i))
        }
        return authors
    }

    fun mockEntity(number: Int): Author {
        val author = Author()
        author.author = "Author Test$number"
        author.price = 25.00
        author.id = number.toLong()
        author.title = "Title Test$number"
        return author
    }

    fun mockVO(number: Int): AuthorVO {
        val author = AuthorVO()
        author.author = "Author Test$number"
        author.price = 25.00
        author.key = number.toLong()
        author.title = "Title Test$number"
        return author
    }
}
