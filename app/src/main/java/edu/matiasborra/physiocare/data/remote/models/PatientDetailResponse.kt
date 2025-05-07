package edu.matiasborra.physiocare.data.remote.models

data class PatientDetailResponse(
    val patient: PatientItem,
    val records: List<RecordItem>
)