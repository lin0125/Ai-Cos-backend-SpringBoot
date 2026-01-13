package com.example.aicosbackendspringboot.dtos.fastAPI;

import java.util.List;

public record ModelInventoryResponse(
        List<ModelMeta> rt_candidates,
        List<ModelMeta> pw_candidates
) {}