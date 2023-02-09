package br.com.seventh.dguardcloud.utils


object Constants {
    var API_SERVER_CAMERAS = ""
    const val API_RESPONSE_OK: Int = 200
    const val API_RESPONSE_OK2: Int = 201
    const val API_RESPONSE_CONTINUE: Int = 100
    const val SwitchingProtocols: Int = 101
    const val API_RESPONSE_Processamento: Int = 102

    /* 2xx success */
    const val API_RESPONSE_CREATED: Int = 201
    const val API_RESPONSE_ACCEPTED: Int = 202
    const val API_RESPONSE_NonAuthoritative: Int = 203
    const val API_RESPONSE_NoContent: Int = 204
    const val API_RESPONSE_ResetContent: Int = 205
    const val API_RESPONSE_PartialContent: Int = 206
    const val API_RESPONSE_MultiStatus: Int = 207

    /* 3xx redirection */
    const val API_RESPONSE_MULTIPLECHOICES: Int = 300
    const val API_RESPONSE_MOVEDPERMANENTLY: Int = 301
    const val API_RESPONSE_MOVEDTEMPORARILY: Int = 302
    const val API_RESPONSE_SEEOTHER: Int = 303
    const val API_RESPONSE_NOTMODIFIED: Int = 304
    const val API_RESPONSE_USEPROXY: Int = 305
    const val API_RESPONSE_SwitchProxy: Int = 306
    const val API_RESPONSE_TEMPORARYREDIRECT: Int = 307
    const val API_RESPONSE_PERMANENTREDIRECT: Int = 308

    /*	4xx Erro de cliente  */
    const val API_RESPONSE_BADREQUEST: Int = 400
    const val API_RESPONSE_UNAUTHORIZED: Int = 401
    const val API_RESPONSE_PAYMENTREQUIRED: Int = 402
    const val API_RESPONSE_FORBIDDEN: Int = 403
    const val API_RESPONSE_NOTFOUND: Int = 404
    const val API_RESPONSE_METHODNOTALLOWED: Int = 405
    const val API_RESPONSE_NOTACCEPTABLE: Int = 406
    const val API_RESPONSE_PROXYAUTHENTICATIONREQUIRED: Int = 407
    const val API_RESPONSE_REQUESTTIMEOUT: Int = 408
    const val API_RESPONSE_CONFLICT: Int = 409
    const val API_RESPONSE_GONE: Int = 410
    const val API_RESPONSE_LENGTHREQUIRED: Int = 411
    const val API_RESPONSE_PRECONDITIONFAILED: Int = 412
    const val API_RESPONSE_PAYLOADTOOLARGE: Int = 413
    const val API_RESPONSE_URITOOLONG: Int = 414
    const val API_RESPONSE_UNSUPPORTEDMEDIATYPE: Int = 415
    const val API_RESPONSE_RANGENOTSATISFIABLE: Int = 416
    const val API_RESPONSE_EXPECTATIONFAILED: Int = 417
    const val API_RESPONSE_IMATEAPOT: Int = 418
    const val API_RESPONSE_MISDIRECTEDREQUEST: Int = 421
    const val API_RESPONSE_UNPROCESSABLEENTITY: Int = 422
    const val API_RESPONSE_LOCKED: Int = 423
    const val API_RESPONSE_FAILEDDEPENDENCY: Int = 424
    const val API_RESPONSE_TOOEARLY: Int = 425
    const val API_RESPONSE_UPGRADEREQUIRED: Int = 426
    const val API_RESPONSE_PRECONDITIONREQUIRED: Int = 428
    const val API_RESPONSE_TOOMANYREQUESTS: Int = 429
    const val API_RESPONSE_REQUESTHEADERFIELDSTOOLARGE: Int = 431
    const val API_RESPONSE_UNAVAILABLEFORLEGALREASONS: Int = 451

    /*	5xx outros erros  */
    const val API_RESPONSE_INTERNALSERVERERROR: Int = 500
    const val API_RESPONSE_NOTIMPLEMENTED: Int = 501
    const val API_RESPONSE_BADGATEWAY: Int = 502
    const val API_RESPONSE_SERVICEUNAVAILABLE: Int = 503
    const val API_RESPONSE_GATEWAYTIMEOUT: Int = 504
    const val API_RESPONSE_HTTPVERSIONNOTSUPPORTED: Int = 505
    const val API_RESPONSE_VARIANTALSONEGOTIATES: Int = 506
    const val API_RESPONSE_INSUFFICIENTSTORAGE: Int = 507
    const val API_RESPONSE_LOOPDETECTED: Int = 508
    const val API_RESPONSE_NOTEXTENDED: Int = 510
    const val API_RESPONSE_NETWORKAUTHENTICATIONREQUIRED: Int = 511

    const val API_RESPONSE_MESSAGE_INVALID_TOKEN = "Token inválido."
    const val API_TOKEN = "X-Access-Token"
    const val API_FIELD_ERROR_BODY = "message"
    const val API_ERROR_CALL_SERVER = "Erro ao chamar o servidor"

    const val ONE_SECOND_IN_MILLISECONDS = 1000
    const val DURATION_TIME_BAR_LIVE_FAKE: Long = 1441
    const val ONE_DAY_IN_SECONDS = 86400
    const val ONE_SECOND = 1

}
