package com.supernova.services

import com.supernova.repository.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class UserServices(@field:Autowired var repository: UsersRepository) : UserDetailsService {

    private val logger = Logger.getLogger(UserServices::class.java.name)

    override fun loadUserByUsername(username : String?) : UserDetails {
        logger.info("finding a user by username $username for you...")
        val user = repository.findByUsername(username)
        return user ?: throw UsernameNotFoundException("$username not found")
    }
}
