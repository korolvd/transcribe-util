package ru.korolvd.transcribe;

import ru.korolvd.transcribe.config.Config;
import ru.korolvd.transcribe.service.FFMPEGProcessor;
import ru.korolvd.transcribe.service.S3Client;
import ru.korolvd.transcribe.service.SpeechKitService;

public class Main {
    public static void main(String[] args) {
        Config config = new Config("cfg/transcribe.properties", args);
        FFMPEGProcessor ffmpegProcessor = new FFMPEGProcessor(config);
        double duration = ffmpegProcessor.extractAudio(config.getValue(Config.AUDIO_FILE));
        S3Client s3Client = new S3Client(config);
        String fileId = s3Client.upload(config.getValue(Config.AUDIO_FILE));
        SpeechKitService speechService = new SpeechKitService(config);
        speechService.recognize(fileId, duration);
    }
}