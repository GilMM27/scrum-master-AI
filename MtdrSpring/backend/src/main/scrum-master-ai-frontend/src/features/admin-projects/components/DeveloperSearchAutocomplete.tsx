import { Autocomplete, CircularProgress, TextField } from "@mui/material";
import type { DeveloperSummary } from "../types/adminProjects.types";

interface DeveloperSearchAutocompleteProps {
  developers: DeveloperSummary[];
  loading: boolean;
  value: DeveloperSummary | null;
  onSelect: (developer: DeveloperSummary | null) => void;
}

const DeveloperSearchAutocomplete = ({
  developers,
  loading,
  value,
  onSelect,
}: DeveloperSearchAutocompleteProps) => {
  return (
    <Autocomplete
      options={developers}
      value={value}
      loading={loading}
      getOptionLabel={(option) => option.email}
      isOptionEqualToValue={(option, val) => option.userId === val.userId}
      onChange={(_, newValue) => onSelect(newValue)}
      renderInput={(params) => {
        const { slotProps: paramsSlotProps, ...restParams } = params;
        return (
          <TextField
            {...restParams}
            label="Buscar desarrollador"
            placeholder="Buscar por email del desarrollador"
            slotProps={{
              ...paramsSlotProps,
              input: {
                ...paramsSlotProps?.input,
                endAdornment: (
                  <>
                    {loading ? (
                      <CircularProgress color="inherit" size={18} />
                    ) : null}
                    {paramsSlotProps?.input?.endAdornment}
                  </>
                ),
              },
            }}
          />
        );
      }}
      noOptionsText="No se encontraron desarrolladores."
    />
  );
};

export default DeveloperSearchAutocomplete;
