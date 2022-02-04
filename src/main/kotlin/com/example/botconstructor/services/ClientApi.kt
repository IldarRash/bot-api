package com.example.botconstructor.services

import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service


@Service
class ClientApi(val clientApiRequester: RSocketRequester) {


}

