/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.imademo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.LocalMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import java.util.UUID;

/**
 * Manages the {@link ExoPlayer}, the IMA plugin and all video playback.
 */
/* package */ final class PlayerManager {

    private SimpleExoPlayer player;
    private long contentPosition;
    private Handler mainHandler;
    private EventLogger eventLogger;
    private DefaultTrackSelector trackSelector;
    private Context context;

    public PlayerManager(Context context) {
        this.context = context;
        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        eventLogger = new EventLogger(trackSelector);
        mainHandler = new Handler();
        @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
        try {
            drmSessionManager = buildDrmSessionManager(context);
        } catch (UnsupportedDrmException e) {
            e.printStackTrace();
        }
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context,drmSessionManager, extensionRendererMode);
        //DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);

    }

    public void init(Context context, SimpleExoPlayerView simpleExoPlayerView) {
        simpleExoPlayerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = getDataSourceFactory(context);
        MediaSource contentMediaSource =
                new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(dataSourceFactory.createDataSource().getUri());

        player.addListener(new PlayerEventListener());
        player.addListener(eventLogger);
        player.addMetadataOutput(eventLogger);
        player.addAudioDebugListener(eventLogger);
        player.addVideoDebugListener(eventLogger);
        player.seekTo(contentPosition);
        player.setPlayWhenReady(true);
        player.prepare(contentMediaSource);
    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(Context context) throws UnsupportedDrmException {
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
        String drmScheme = "clearkey";
        UUID uuid = DemoUtil.getDrmUuid(drmScheme);
        
        LocalMediaDrmCallback drmCallback =
                new LocalMediaDrmCallback(
                       // "{\"keys\":[{\"kty\":\"oct\",\"k\":\"CiR7B1HL8agn4v7fuHR5og\",\"kid\":\"kTQZUWlrXhuiMkOezsHxKg\"}],\"type\":\"temporary\"}".getBytes());
        "{\"keys\":[{\"kty\":\"oct\",\"k\":\"NjllYWE4MDJhNjc2M2FmOTc5ZThkMTk0MGZiODgzOTI=\",\"kid\":\"YWJiYTI3MWU4YmNmNTUyYmJkMmU4NmE0MzRhOWE1ZDk=\"}],\"type\":\"temporary\"}".getBytes());
        FrameworkMediaDrm fmDrm = FrameworkMediaDrm.newInstance(uuid);
        drmSessionManager = new DefaultDrmSessionManager<FrameworkMediaCrypto>(
                uuid,
                fmDrm,
                drmCallback,
                null,
                mainHandler,
                eventLogger);
        return drmSessionManager;
    }

    private DataSource.Factory getDataSourceFactory(Context context) {
        DataSource.Factory dataSourceFactory = null;
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.encrypted));
        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(context);
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }

        dataSourceFactory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return rawResourceDataSource;
            }
        };
        return dataSourceFactory;
    }

    public void reset() {
        if (player != null) {
            contentPosition = player.getContentPosition();
            player.release();
            player = null;
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
        //adsLoader.release();
    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {

        }
    }
}
