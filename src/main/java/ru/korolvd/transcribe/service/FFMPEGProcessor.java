package ru.korolvd.transcribe.service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import ru.korolvd.transcribe.config.Config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FFMPEGProcessor {
    private final String videoFile;

    public FFMPEGProcessor(Config config) {
        this.videoFile = config.getValue(Config.VIDEO_FILE);
    }

    public double extractAudio(String audioFile) {
        double duration;
        try {
            FFmpeg ffmpeg = new FFmpeg("ffmpeg/ffmpeg.exe");
            FFprobe ffprobe = new FFprobe("ffmpeg/ffprobe.exe");
            FFmpegProbeResult in = ffprobe.probe(videoFile);
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(in)
                    .addOutput(audioFile)
                    .done();
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            FFmpegJob job = executor.createJob(builder, new ProgressListener() {
                final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
                @Override
                public void progress(Progress progress) {
                    System.out.print('\r');
                    double percentage = progress.out_time_ns / duration_ns;
                    System.out.printf('\r' +
                            "[%.0f%%] status:%s bitrate:%d time:%s ms size:%d Kb speed:%.2fx",
                            percentage * 100,
                            progress.status,
                            progress.bitrate,
                            FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                            progress.total_size / 1024,
                            progress.speed
                    );
                }
            });
            System.out.println("Extracting audio from " + videoFile);
            job.run();
            System.out.println("Done");
            System.out.println();
            duration = in.format.duration;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return duration;
    }
}
