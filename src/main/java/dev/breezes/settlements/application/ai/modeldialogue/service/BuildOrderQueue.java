package dev.breezes.settlements.application.ai.modeldialogue.service;

import dev.breezes.settlements.compat.create.build.BuildPlan;
import dev.breezes.settlements.compat.create.build.BuildingSiteFinder;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import jakarta.inject.Singleton;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Deque;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 村民「模型对话建造」的待建任务队列（服务端单例）。
 * <p>
 * 玩家在对话界面批准一份蓝图后，蓝图被 {@link #submit} 入队并自动选址；空闲的建筑师/机械师通过
 * {@link #claimNext} 认领一份未认领的任务，逐格盖完后再 {@link #complete} 出队。
 */
@CustomLog
@Singleton
public final class BuildOrderQueue {

    /** 单份任务：选址原点 + 蓝图 + 认领状态。 */
    public static final class PendingJob {
        public final UUID jobId;
        public final BlockPos origin;
        public final BuildPlan plan;
        @Nullable
        public final UUID requestedBy;
        public boolean claimed;
        @Nullable
        public UUID claimedBy;

        public PendingJob(@Nonnull UUID jobId, @Nonnull BlockPos origin, @Nonnull BuildPlan plan, @Nullable UUID requestedBy) {
            this.jobId = jobId;
            this.origin = origin;
            this.plan = plan;
            this.requestedBy = requestedBy;
            this.claimed = false;
            this.claimedBy = null;
        }
    }

    private final Deque<PendingJob> jobs = new ConcurrentLinkedDeque<>();

    public BuildOrderQueue() {
    }

    /**
     * 入队一份蓝图：在其附近自动寻找平坦空地作为建造原点。
     *
     * @return 新建任务的 id（即使选址失败也会以村民位置附近为原点入队，保证可建）
     */
    @Nonnull
    public UUID submit(@Nonnull BuildPlan plan, @Nonnull ServerLevel level, @Nonnull BlockPos near) {
        int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;
        for (BuildPlan.PlaceEntry entry : plan.entries()) {
            BlockPos o = entry.offset();
            minX = Math.min(minX, o.getX()); minY = Math.min(minY, o.getY()); minZ = Math.min(minZ, o.getZ());
            maxX = Math.max(maxX, o.getX()); maxY = Math.max(maxY, o.getY()); maxZ = Math.max(maxZ, o.getZ());
        }
        int footprint = (maxX - minX + 1);
        int height = (maxY - minY + 1);

        BlockPos origin = near;
        Optional<BlockPos> site = BuildingSiteFinder.find(level, near, 28, footprint, height);
        if (site.isPresent()) {
            origin = site.get().offset(-minX, -minY, -minZ);
        } else {
            log.warn("No suitable build site found near {}, building at approximate location", near);
        }

        PendingJob job = new PendingJob(UUID.randomUUID(), origin, plan, null);
        this.jobs.addLast(job);
        log.info("Enqueued model blueprint with {} blocks at {}", plan.size(), origin);
        return job.jobId;
    }

    /**
     * 由村民认领下一份未认领的任务。
     */
    @Nonnull
    public Optional<PendingJob> claimNext(@Nonnull BaseVillager villager) {
        UUID id = villager.getUUID();
        for (PendingJob job : this.jobs) {
            if (!job.claimed) {
                job.claimed = true;
                job.claimedBy = id;
                return Optional.of(job);
            }
        }
        return Optional.empty();
    }

    public void complete(@Nonnull UUID jobId) {
        this.jobs.removeIf(job -> job.jobId.equals(jobId));
    }

    public int size() {
        return this.jobs.size();
    }
}
