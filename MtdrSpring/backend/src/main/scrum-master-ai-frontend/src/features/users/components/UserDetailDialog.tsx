import { alpha, Dialog, DialogContent, DialogTitle, Divider, IconButton, Stack, Typography } from "@mui/material";
import type { UserDetail } from "../types/users.types"
import { CloseRounded } from "@mui/icons-material";

interface UserDetailDialogProps {
  open: boolean;
  userDetail: UserDetail | null;
  onClose: () => void;
};

const DetailRow = ({ label, value }: { label: string; value: string | null | undefined }) => (
  <Stack spacing={0.5}>
    <Typography variant="caption" color="text.secondary">
      {label}
    </Typography>
    <Typography variant="body1" color="text.primary">
      {value || 'No disponible'}
    </Typography>
  </Stack>
);

const UserDetailDialog = ({ open, userDetail, onClose }: UserDetailDialogProps) => {
  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <Stack direction="row" sx={{ alignItems: 'center', justifyContent: 'space-between', bgcolor: 'primary.dark', px: 2, py: 1 }}>
          <DialogTitle>Detalle de Usuario</DialogTitle>
          <IconButton onClick={onClose} size="small" sx={{ mr: 2, '&:hover': { color: alpha('#b4bdc7', 0.8) } }}>
            <CloseRounded />
          </IconButton>
      </Stack>

      <DialogContent dividers>
        {!userDetail ? (
          <Typography color="text.secondary">
            No hay detalles disponibles para este usuario.
          </Typography>
        ) : (
          <Stack spacing={2}>
            <DetailRow label="ID" value={userDetail.userId} />
            <Divider />
            <DetailRow label="Username" value={userDetail.username} />
            <DetailRow label="Email" value={userDetail.email} />
            <DetailRow label="Rol" value={userDetail.userRole} />
            <DetailRow label="Estado" value={userDetail.accountStatus} />
            <DetailRow label="Telegram ID" value={userDetail.telegramId} />
            <DetailRow label="Celular" value={userDetail.cellPhone} />
            <DetailRow label="Creado el" value={new Date(userDetail.createdAt).toLocaleString()} />
          </Stack>
        )}
      </DialogContent>
    </Dialog>
  )
}

export default UserDetailDialog